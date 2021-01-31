package net.smackem.jobotwar.io.tcp;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

public class LocalTcpServerTest {

    private static final int PORT = 55556;

    @Test
    public void testLocalServerSubscription() throws IOException, InterruptedException {
        final Collection<TestMessage.Base> receivedMessages = new ArrayList<>();
        final Collection<TestMessage.Base> messagesToSend = generateMessages(10_000);
        final CountDownLatch latch = new CountDownLatch(1);
        try (final LocalTcpServer<TestMessage.Base> server = new LocalTcpServer<>(PORT, TestProtocol::new)) {
            server.messageReceivedEvent().subscribe(item -> {
                receivedMessages.add(item.message());
                if (receivedMessages.size() % 500 == 0) {
                    System.out.printf("%d messages received\n", receivedMessages.size());
                }
            });
            server.clientDisconnectedEvent().subscribe(ignored -> latch.countDown());
            connectAndWrite(messagesToSend);
            assertThat(latch.await(1, TimeUnit.SECONDS)).isTrue();
        }
        assertThat(receivedMessages)
                .hasSize(messagesToSend.size())
                .containsExactly(messagesToSend.toArray(TestMessage.Base[]::new));
    }

    @Test
    public void testLocalServerDispatchToClient() throws IOException, InterruptedException {
        final Collection<TestMessage.Base> messagesToSend = generateMessages(10_000);
        Collection<TestMessage.Base> receivedMessages;
        try (final LocalTcpServer<TestMessage.Base> server = new LocalTcpServer<>(PORT, TestProtocol::new)) {
            server.messageReceivedEvent().subscribe(msg -> server.broadcastMessage(msg.message(), msg.origin()));
            final var future = connectAndRead(messagesToSend.size());
            connectAndWrite(messagesToSend);
            try {
                receivedMessages = future.get(10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                receivedMessages = null;
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        assertThat(receivedMessages)
                .isNotNull()
                .hasSize(messagesToSend.size())
                .containsExactly(messagesToSend.toArray(TestMessage.Base[]::new));
    }

    // on a MacBook pro 2016, this takes ~4 sec with loglevel info
    @Test
    public void testLocalServerTiming() throws IOException, InterruptedException {
        for (int i = 0; i < 50; i++) {
            testLocalServerSubscription();
        }
    }

    private Collection<TestMessage.Base> generateMessages(int count) {
        final Collection<TestMessage.Base> messages = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            messages.add(new TestMessage.Chat(String.valueOf(i)));
        }
        return messages;
    }

    private void connectAndWrite(Collection<TestMessage.Base> messages) throws IOException {
        final SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(true);
        channel.connect(new InetSocketAddress("localhost", PORT));
        final Protocol<TestMessage.Base> protocol = new TestProtocol();
        int count = 0;
        for (final var message : messages) {
            final ByteBuffer bytes = protocol.encodeMessage(message);
            channel.write(bytes);
            count++;
            if (count % 500 == 0) {
                System.out.printf("%d messages sent\n", count);
            }
        }
        channel.close();
    }

    private CompletableFuture<Collection<TestMessage.Base>> connectAndRead(int messageCount) throws IOException {
        final SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(true);
        channel.connect(new InetSocketAddress("localhost", PORT));
        return CompletableFuture.supplyAsync(() -> {
            try {
                final Collection<TestMessage.Base> messages = new ArrayList<>();
                final Protocol<TestMessage.Base> protocol = new TestProtocol();
                final ByteBuffer buffer = ByteBuffer.allocate(1024);
                loop: while (true) {
                    final int byteCount = channel.read(buffer);
                    for (int i = 0; i < byteCount; i++) {
                        final TestMessage.Base message = protocol.readByte(buffer.get(i));
                        if (message != null) {
                            messages.add(message);
                            if (messages.size() >= messageCount) {
                                break loop;
                            }
                        }
                    }
                    buffer.clear();
                }
                channel.close();
                return messages;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}