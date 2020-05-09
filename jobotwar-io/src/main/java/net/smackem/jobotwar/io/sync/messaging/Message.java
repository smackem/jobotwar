package net.smackem.jobotwar.io.sync.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class Message {

    private static final Logger log = LoggerFactory.getLogger(Message.class);

    public final void encode(OutputStream os) {
        final JsonGenerator json = Json.createGenerator(os)
                .writeStartObject();
        encode(json);
        json.writeEnd().close();
    }

    abstract void encode(JsonGenerator json);

    public static Message decode(InputStream is) {
        final JsonReader json = Json.createReader(is);
        final JsonObject message = json.readObject();
        for (final var entry : message.entrySet()) {
            switch (entry.getKey()) {
                case "init" -> {
                    return decodeInitMessage(entry.getValue().asJsonObject());
                }
                case "game" -> {
                    return decodeGameMessage(entry.getValue().asJsonObject());
                }
                case "turn" -> {
                    return decodeTurnMessage(entry.getValue().asJsonObject());
                }
                default -> {
                    log.warn("ignoring unknown message key '{}'", entry.getKey());
                }
            }
        }
        return null;
    }

    private static Message decodeInitMessage(JsonObject message) {
        return null;
    }

    private static Message decodeGameMessage(JsonObject message) {
        return null;
    }

    private static Message decodeTurnMessage(JsonObject message) {
        return null;
    }
}
