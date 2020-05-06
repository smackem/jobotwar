package net.smackem.jobotwar.io.server;

import net.smackem.jobotwar.io.events.EventPublisher;
import net.smackem.jobotwar.io.events.SimpleEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LocalServer<TMessage> implements AutoCloseable {
    private final Logger log = LoggerFactory.getLogger(LocalServer.class);
    private final Object monitor = new Object();
    private final SimpleEventPublisher<TMessage> messageReceived = new SimpleEventPublisher<>();
    private final SimpleEventPublisher<SocketAddress> clientConnected = new SimpleEventPublisher<>();
    private final SimpleEventPublisher<SocketAddress> clientDisconnected = new SimpleEventPublisher<>();
    private final int port;
    private final Selector selector;
    private final ServerSocketChannel acceptChannel;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
    private final Collection<RemoteClient<TMessage>> clients = new ArrayList<>();
    private final ExecutorService ioExecutorService;
    private final Supplier<Protocol<TMessage>> protocolFactory;
    private final Boolean tcpNoDelay;
    private volatile boolean closed;

    public LocalServer(int port, Supplier<Protocol<TMessage>> protocolFactory, boolean tcpNoDelay) throws IOException {
        this.port = port;
        this.protocolFactory = Objects.requireNonNull(protocolFactory);
        this.tcpNoDelay = tcpNoDelay;
        this.ioExecutorService = Executors.newSingleThreadExecutor();
        this.selector = Selector.open();
        this.acceptChannel = ServerSocketChannel.open()
                .bind(new InetSocketAddress(this.port));
        this.acceptChannel.configureBlocking(false)
                .register(this.selector, this.acceptChannel.validOps());
        this.ioExecutorService.submit(this::run);
    }

    public LocalServer(int port, Supplier<Protocol<TMessage>> protocolFactory) throws IOException {
        this(port, protocolFactory, false);
    }

    public final EventPublisher<TMessage> messageReceivedEvent() {
        return this.messageReceived;
    }

    public final EventPublisher<SocketAddress> clientConnectedEvent() {
        return this.clientConnected;
    }

    public final EventPublisher<SocketAddress> clientDisconnectedEvent() {
        return this.clientDisconnected;
    }

    public Collection<SocketAddress> connectedClients() {
        synchronized (this.monitor) {
            return this.clients.stream()
                    .map(RemoteClient::address)
                    .collect(Collectors.toList());
        }
    }

    public boolean isAccepting() {
        return this.acceptChannel.isOpen();
    }

    public void stopAccepting() throws IOException {
        this.acceptChannel.close();
    }

    private void run() {
        log.debug("enter RUN");
        try {
            next();
        } catch (IOException | ClosedSelectorException e) {
            log.info("I/O broke off", e);
            return;
        }
        if (this.closed) {
            log.info("I/O closed gracefully");
            return;
        }
        this.ioExecutorService.submit(this::run);
        log.debug("exit RUN");
    }

    private void next() throws IOException {
        if (this.selector.select() == 0) {
            // wakeup called or channel closed
            return;
        }
        final var iterator = this.selector.selectedKeys().iterator();
        while (iterator.hasNext()) {
            final var key = iterator.next();
            if (key.isAcceptable()) {
                assert key.channel() == this.acceptChannel;
                final SocketChannel clientChannel = this.acceptChannel.accept();
                log.info("accepted {}", clientChannel);
                addClient(clientChannel);
            } else if (key.isReadable()) {
                @SuppressWarnings("unchecked")
                final RemoteClient<TMessage> client = (RemoteClient<TMessage>) key.attachment();
                assert client.channel() == key.channel();
                if (client.read(this.buffer)) {
                    log.debug("read {} bytes from {}", this.buffer.position(), client);
                    this.buffer.clear();
                } else {
                    log.info("I/O end from {}", client);
                    closeClient(client);
                }
            }
            iterator.remove();
        }
    }

    private void addClient(SocketChannel channel) {
        final RemoteClient<TMessage> client = new RemoteClient<>(channel, this.protocolFactory.get(), this);
        try {
            channel.setOption(StandardSocketOptions.TCP_NODELAY, this.tcpNoDelay)
                    .configureBlocking(false)
                    .register(this.selector, SelectionKey.OP_READ)
                    .attach(client);
        } catch (IOException e) {
            log.error("error adding client", e);
            return;
        }
        synchronized (this.monitor) {
            this.clients.add(client);
        }
        this.clientConnected.submit(client.address());
    }

    private void closeClient(RemoteClient<TMessage> client) {
        log.debug("close client {}", client);
        try {
            client.close();
        } catch (IOException e) {
            log.warn("error closing client", e);
        }
        synchronized (this.monitor) {
            this.clients.remove(client);
        }
        this.clientDisconnected.submit(client.address());
    }

    void handleMessage(TMessage message, RemoteClient<TMessage> origin) {
        this.messageReceived.submit(message);
        final Collection<RemoteClient<TMessage>> clients;
        synchronized (this.monitor) {
            clients = List.copyOf(this.clients);
        }
        for (final RemoteClient<TMessage> client : clients) {
            if (client != origin) {
                writeMessage(message, client);
            }
        }
    }

    void writeMessage(TMessage message, RemoteClient<TMessage> destination) {
        try {
            destination.write(message);
        } catch (IOException e) {
            closeClient(destination);
        }
    }

    @Override
    public void close() throws IOException {
        log.info("close server");
        this.closed = true;
        this.acceptChannel.close();
        synchronized (this.monitor) {
            for (final RemoteClient<TMessage> client : this.clients) {
                try {
                    client.close();
                } catch (IOException ignored) {
                    // nothing to do
                }
            }
            this.clients.clear();
        }
        this.selector.close();
        this.ioExecutorService.shutdown();
        try {
            this.ioExecutorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "LocalServer{" +
               "port=" + port +
               ", client_count=" + clients.size() +
               '}';
    }
}
