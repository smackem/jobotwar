package net.smackem.jobotwar.io.tcp;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TestProtocol implements Protocol<TestMessage.Base> {
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    @Override
    public TestMessage.Base readByte(byte b) {
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
    public ByteBuffer encodeMessage(TestMessage.Base message) {
        final String text;
        if (message instanceof TestMessage.Chat chat) {
            text = chat.text() + '\n';
        } else if (message instanceof TestMessage.ClientConnected clientConnected) {
            text = "/" + clientConnected.clientAddress();
        } else {
            text = "\n";
        }
        return ByteBuffer.wrap(text.getBytes(StandardCharsets.UTF_8));
    }

    private TestMessage.Base decodeMessage() {
        final byte[] bytes = this.bos.toByteArray();
        this.bos.reset();
        if (bytes.length == 0) {
            return new TestMessage.Chat("");
        }
        //noinspection SwitchStatementWithTooFewBranches
        return switch (bytes[0]) {
            case '/' -> new TestMessage.ClientConnected(
                    new String(bytes, 1, bytes.length - 1, StandardCharsets.UTF_8));
            default -> new TestMessage.Chat(new String(bytes, StandardCharsets.UTF_8).trim());
        };
    }
}
