package net.smackem.jobotwar.io.sync.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

class MessageTests {
    private MessageTests() {
        throw new IllegalAccessError();
    }

    static InputStream createStringInput(String str) {
        final byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }

    static byte[] encodeMessage(Message message) {
        final var stream = new ByteArrayOutputStream();
        message.encode(stream);
        return stream.toByteArray();
    }
}
