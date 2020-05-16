package net.smackem.jobotwar.io.sync;

import net.smackem.jobotwar.io.sync.messaging.*;
import net.smackem.jobotwar.io.tcp.Protocol;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SyncProtocolTest {
    @Test
    public void encode() {
        final Message message = new GameMessage(GameStatus.PLAY);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(SyncProtocol.STX);
        message.encode(bos);
        bos.write(SyncProtocol.ETX);
        final byte[] bytes = bos.toByteArray();
        assertThat(bytes).startsWith(SyncProtocol.STX);
        final String json = new String(bytes, 1, bytes.length - 2, StandardCharsets.UTF_8);
        assertThat(json).startsWith("{");
        assertThat(json).endsWith("}");
        assertThat(json).contains(GameStatus.PLAY.name());
        assertThat(bytes).endsWith(SyncProtocol.ETX);
    }

    @Test
    public void decode() {
        final Protocol<Message> protocol = new SyncProtocol();
        final Message message = new GameMessage(GameStatus.PLAY);
        final ByteBuffer buffer = protocol.encodeMessage(message);
        //final InputStream is = new ByteArrayInputStream(buffer.array(), buffer.arrayOffset(), buffer.remaining());
        final Collection<Message> decodedMessages = readMessages(buffer, protocol);
        assertThat(decodedMessages).hasSize(1);
        final Message decodedMessage = decodedMessages.iterator().next();
        assertThat(decodedMessage).isInstanceOf(GameMessage.class);
        assertThat(((GameMessage)decodedMessage).status()).isEqualTo(GameStatus.PLAY);
    }

    @Test
    public void decodeMultiple() {
        final Protocol<Message> protocol = new SyncProtocol();
        final ByteBuffer buffer1 = protocol.encodeMessage(new GameMessage(GameStatus.PLAY));
        final ByteBuffer buffer2 = protocol.encodeMessage(new GameMessage(GameStatus.PAUSE));
        final ByteBuffer buffer3 = protocol.encodeMessage(new TurnMessage.Builder(99)
                .addRobotStateChange(UUID.randomUUID(), new RobotStateChange.Builder()
                        .shot(1000)
                        .build())
                .build());
        final Collection<Message> decodedMessages = readMessages(
                joinBuffers(null, buffer1, buffer2, buffer3), protocol);
        assertThat(decodedMessages).hasSize(3);
        assertThat(decodedMessages).hasOnlyElementsOfTypes(GameMessage.class, TurnMessage.class);
        assertThat(decodedMessages).last().isInstanceOf(TurnMessage.class);
    }

    @Test
    public void decodeMultipleWithSalt() {
        final Protocol<Message> protocol = new SyncProtocol();
        final ByteBuffer buffer1 = protocol.encodeMessage(new GameMessage(GameStatus.PLAY));
        final ByteBuffer buffer2 = protocol.encodeMessage(new GameMessage(GameStatus.PAUSE));
        final ByteBuffer buffer3 = protocol.encodeMessage(new TurnMessage.Builder(99)
                .addRobotStateChange(UUID.randomUUID(), new RobotStateChange.Builder()
                        .shot(1000)
                        .build())
                .build());
        final Collection<Message> decodedMessages = readMessages(
                joinBuffers(new byte[] { 101, 102, 103 }, buffer1, buffer2, buffer3), protocol);
        assertThat(decodedMessages).hasSize(3);
        assertThat(decodedMessages).hasOnlyElementsOfTypes(GameMessage.class, TurnMessage.class);
        assertThat(decodedMessages).last().isInstanceOf(TurnMessage.class);
    }

    private static Collection<Message> readMessages(ByteBuffer buffer, Protocol<Message> protocol) {
        final Collection<Message> messages = new ArrayList<>();
        while (buffer.remaining() > 0) {
            final Message decodedMessage = protocol.readByte(buffer.get());
            if (decodedMessage != null) {
                messages.add(decodedMessage);
            }
        }
        return messages;
    }

    private static ByteBuffer joinBuffers(byte[] separator, ByteBuffer... buffers) {
        int length = Arrays.stream(buffers).mapToInt(ByteBuffer::remaining).sum();
        if (separator != null) {
            length += separator.length * (buffers.length - 1);
        }
        final ByteBuffer target = ByteBuffer.allocate(length);
        boolean first = true;
        for (final ByteBuffer source : buffers) {
            if (first == false && separator != null) {
                target.put(separator);
            }
            target.put(source);
            first = false;
        }
        return target.flip();
    }
}