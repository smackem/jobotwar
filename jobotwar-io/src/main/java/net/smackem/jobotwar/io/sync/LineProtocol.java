package net.smackem.jobotwar.io.sync;

import net.smackem.jobotwar.io.tcp.Protocol;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class LineProtocol implements Protocol<Message.Base> {
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    @Override
    public Message.Base readByte(byte b) {
        switch (b) {
            case '\n' -> {
                return decodeMessage();
            }
            case '\r' -> {}
            default -> bos.write(b);
        }
        return null;
    }

    @Override
    public ByteBuffer encodeMessage(Message.Base message) {
        final String text;
        if (message instanceof Message.Chat chat) {
            text = chat.text() + '\n';
        } else if (message instanceof Message.ClientConnected clientConnected) {
            text = "/" + clientConnected.clientAddress();
        } else {
            text = "\n";
        }
        return ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));
    }

    private Message.Base decodeMessage() {
        final byte[] bytes = this.bos.toByteArray();
        this.bos.reset();
        if (bytes.length == 0) {
            return new Message.Chat("");
        }
        //noinspection SwitchStatementWithTooFewBranches
        return switch (bytes[0]) {
            case '/' -> new Message.ClientConnected(
                    new String(bytes, 1, bytes.length - 1, StandardCharsets.UTF_8));
            default -> new Message.Chat(new String(bytes, StandardCharsets.UTF_8).trim());
        };
    }
}
