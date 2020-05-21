package net.smackem.jobotwar.io.sync;

import net.smackem.jobotwar.io.sync.messaging.Message;
import net.smackem.jobotwar.io.tcp.LocalTcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketAddress;

public class LocalSyncServer implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(LocalSyncServer.class);
    private static final int PORT = 55555;
    private final LocalTcpServer<Message> tcpServer;

    public LocalSyncServer() throws IOException {
        this.tcpServer = new LocalTcpServer<>(PORT, SyncProtocol::new);
        this.tcpServer.clientConnectedEvent().subscribe(this::onClientConnected);
        this.tcpServer.clientDisconnectedEvent().subscribe(this::onClientDisconnected);
        this.tcpServer.messageReceivedEvent().subscribe(this::onMessageReceived);
    }

    private void onClientConnected(SocketAddress addr) {
    }

    private void onClientDisconnected(SocketAddress addr) {
    }

    private void onMessageReceived(LocalTcpServer.ReceivedMessage<Message> msg) {
    }

    @Override
    public void close() {
        this.tcpServer.close();
    }
}
