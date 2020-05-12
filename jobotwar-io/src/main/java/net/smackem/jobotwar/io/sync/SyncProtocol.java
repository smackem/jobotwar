package net.smackem.jobotwar.io.sync;

import net.smackem.jobotwar.io.sync.messaging.Message;
import net.smackem.jobotwar.io.tcp.Protocol;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class SyncProtocol implements Protocol<Message> {
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    @Override
    public Message readByte(byte b) {
        switch (b) {
            case 0x02 -> {
                return decodeMessage();
            }
            case 0x03 -> {}
            default -> bos.write(b);
        }
        return null;
    }

    @Override
    public ByteBuffer encodeMessage(Message message) {
        //final String text;
        //return ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));
        return null;
    }

    private Message decodeMessage() {
        final byte[] bytes = this.bos.toByteArray();
        this.bos.reset();
        return null;
    }
}
