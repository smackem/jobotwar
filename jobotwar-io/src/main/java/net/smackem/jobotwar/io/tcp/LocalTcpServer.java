package net.smackem.jobotwar.io.tcp;

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

/**
 * An asynchronous tcp listener that manages an unlimited number of clients.
 *
 * @param <TMessage>
 *     The type that implements the communication protocol.
 *
 * @implNote All public methods of this class are thread-safe.
 */
public class LocalTcpServer<TMessage> implements AutoCloseable {
    private final Logger log = LoggerFactory.getLogger(LocalTcpServer.class);
    private final Object monitor = new Object();
    private final SimpleEventPublisher<ReceivedMessage<TMessage>> messageReceived = new SimpleEventPublisher<>();
    private final SimpleEventPublisher<SocketAddress> clientConnected = new SimpleEventPublisher<>();
    private final SimpleEventPublisher<SocketAddress> clientDisconnected = new SimpleEventPublisher<>();
    private final int port;
    private final Selector selector;
    private final ServerSocketChannel acceptChannel;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
    private final Collection<RemoteTcpClient<TMessage>> clients = new ArrayList<>();
    private final ExecutorService ioExecutorService;
    private final Supplier<Protocol<TMessage>> protocolFactory;
    private final Boolean tcpNoDelay;
    private volatile boolean closed;

    /**
     * Initializes a new instance of {@link LocalTcpServer}.
     *
     * @param port
     *      The TCP port to listen on.
     *
     * @param protocolFactory
     *      A {@link java.util.function.Consumer} that creates a {@link Protocol} object.
     *      For every client that connects, one protocol object is created.
     *
     * @param tcpNoDelay
     *      {@code true} if the client sockets should be configured for instant transmission,
     *      otherwise {@code false}.
     *
     * @throws IOException if the tcp listener could not be created.
     */
    public LocalTcpServer(int port, Supplier<Protocol<TMessage>> protocolFactory, boolean tcpNoDelay) throws IOException {
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

    /**
     * Initializes a new instance of {@link LocalTcpServer} with instant transmission disabled
     * (the tcpNoDelay flag is false).
     *
     * @param port
     *      The TCP port to listen on.
     *
     * @param protocolFactory
     *      A {@link java.util.function.Consumer} that creates a {@link Protocol} object.
     *      For every client that connects, one protocol object is created.
     *
     * @throws IOException if the tcp listener could not be created.
     */
    public LocalTcpServer(int port, Supplier<Protocol<TMessage>> protocolFactory) throws IOException {
        this(port, protocolFactory, false);
    }

    /**
     * @return An event publisher that publishes an event every time a message has been received.
     */
    public final EventPublisher<ReceivedMessage<TMessage>> messageReceivedEvent() {
        return this.messageReceived;
    }

    /**
     * Record that contains information about a received message.
     * @param <TMessage> The message type.
     */
    public record ReceivedMessage<TMessage>(SocketAddress origin, TMessage message) {}

    /**
     * @return An event publisher that publishes an event every time a client connects to the server.
     */
    public final EventPublisher<SocketAddress> clientConnectedEvent() {
        return this.clientConnected;
    }

    /**
     * @return An event publisher that publishes an event every time a client disconnects from the server.
     */
    public final EventPublisher<SocketAddress> clientDisconnectedEvent() {
        return this.clientDisconnected;
    }

    /**
     * @return A collection containing the remote addresses of all currently connected clients.
     */
    public Collection<SocketAddress> connectedClients() {
        synchronized (this.monitor) {
            return this.clients.stream()
                    .map(RemoteTcpClient::address)
                    .collect(Collectors.toList());
        }
    }

    /**
     * @return A value indicating whether this tcp server accepts new client connection or not.
     */
    public boolean isAccepting() {
        return this.acceptChannel.isOpen();
    }

    /**
     * Stops accepting new client connections.
     */
    public void stopAccepting() {
        log.info("stopped accepting new client connections");
        try {
            this.acceptChannel.close();
        } catch (IOException e) {
            log.warn("attempted to close accept channel that already was broken", e);
        }
    }

    /**
     * Sends a message to all connected client.
     *
     * @param message
     *      The message to send.
     *
     * @param except
     *      If not {@code null}, excludes the client with the given address from the
     *      broadcast.
     */
    public void broadcastMessage(TMessage message, SocketAddress except) {
        final Collection<RemoteTcpClient<TMessage>> clients;
        synchronized (this.monitor) {
            clients = List.copyOf(this.clients);
        }
        for (final RemoteTcpClient<TMessage> client : clients) {
            if (Objects.equals(client.address(), except) == false) {
                writeMessage(message, client);
            }
        }
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
                final RemoteTcpClient<TMessage> client = (RemoteTcpClient<TMessage>) key.attachment();
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
        final RemoteTcpClient<TMessage> client = new RemoteTcpClient<>(channel, this.protocolFactory.get(), this);
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

    private void closeClient(RemoteTcpClient<TMessage> client) {
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

    void handleMessage(TMessage message, RemoteTcpClient<TMessage> origin) {
        this.messageReceived.submit(new ReceivedMessage<>(origin.address(), message));
    }

    private void writeMessage(TMessage message, RemoteTcpClient<TMessage> destination) {
        try {
            destination.write(message);
        } catch (IOException e) {
            closeClient(destination);
        }
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        log.info("close server");
        this.closed = true;
        this.messageReceived.close();
        this.clientConnected.close();
        this.clientDisconnected.close();
        try {
            this.acceptChannel.close();
        } catch (IOException ignored) {
            // nothing to do
        }
        synchronized (this.monitor) {
            for (final RemoteTcpClient<TMessage> client : this.clients) {
                try {
                    client.close();
                } catch (IOException ignored) {
                    // nothing to do
                }
            }
            this.clients.clear();
        }
        try {
            this.selector.close();
        } catch (IOException ignored) {
            // nothing to do
        }
        this.ioExecutorService.shutdown();
        try {
            this.ioExecutorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.info("interrupted while awaiting io-executor termination", e);
        }
    }

    @Override
    public String toString() {
        return "LocalServer{" +
               "port=" + port +
               '}';
    }
}
