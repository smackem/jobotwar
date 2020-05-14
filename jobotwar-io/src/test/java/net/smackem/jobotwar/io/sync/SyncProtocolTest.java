package net.smackem.jobotwar.io.sync;

import net.smackem.jobotwar.io.sync.messaging.GameMessage;
import net.smackem.jobotwar.io.sync.messaging.GameStatus;
import net.smackem.jobotwar.io.sync.messaging.Message;
import net.smackem.jobotwar.io.tcp.Protocol;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
        while (buffer.remaining() > 0) {
            final Message decodedMessage = protocol.readByte(buffer.get());
        }
    }
}