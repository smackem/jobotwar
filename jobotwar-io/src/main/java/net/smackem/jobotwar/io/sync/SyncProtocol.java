package net.smackem.jobotwar.io.sync;

import net.smackem.jobotwar.io.sync.messaging.Message;
import net.smackem.jobotwar.io.tcp.Protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class SyncProtocol implements Protocol<Message> {

    private static final byte STX = 0x02;
    private static final byte ETX = 0x03;
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private boolean inBody;

    @Override
    public Message readByte(byte b) {
        if (this.inBody) {
            if (b == ETX) {
                this.inBody = false;
                return decodeMessage();
            }
            bos.write(b);
            return null;
        }
        if (b == STX) {
            this.inBody = true;
        }
        return null;
    }

    @Override
    public ByteBuffer encodeMessage(Message message) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(STX);
        message.encode(bos);
        bos.write(ETX);
        return ByteBuffer.wrap(bos.toByteArray());
    }

    private Message decodeMessage() {
        final byte[] bytes = this.bos.toByteArray();
        this.bos.reset();
        return Message.decode(new ByteArrayInputStream(bytes));
    }
}
