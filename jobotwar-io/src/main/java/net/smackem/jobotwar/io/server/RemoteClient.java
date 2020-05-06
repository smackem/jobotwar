package net.smackem.jobotwar.io.server;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SocketChannel;

class RemoteClient<TMessage> implements AutoCloseable {
    private final SocketChannel channel;
    private final Protocol<TMessage> protocol;
    private final LocalServer<TMessage> server;
    private final SocketAddress remoteAddress;

    RemoteClient(SocketChannel channel, Protocol<TMessage> protocol, LocalServer<TMessage> server) {
        this.channel = channel;
        this.protocol = protocol;
        this.server = server;
        SocketAddress remoteAddress;
        try {
            remoteAddress = channel.getRemoteAddress();
        } catch (IOException e) {
            remoteAddress = null;
        }
        this.remoteAddress = remoteAddress;
    }

    Channel channel() {
        return this.channel;
    }

    SocketAddress address() {
        return this.remoteAddress;
    }

    boolean read(ByteBuffer buffer) {
        final int count;
        try {
            count = this.channel.read(buffer);
            if (count < 0) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            final TMessage message = this.protocol.readByte(buffer.get(i));
            if (message != null) {
                this.server.handleMessage(message, this);
            }
        }
        return true;
    }

    void write(TMessage message) throws IOException {
        final ByteBuffer buffer = this.protocol.encodeMessage(message);
        this.channel.write(buffer);
    }

    @Override
    public void close() throws IOException {
        this.channel.close();
    }

    @Override
    public String toString() {
        return "RemoteClient{" + this.remoteAddress + "}";
    }
}
