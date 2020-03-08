package net.smackem.jobotwar.persist;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

public class PersistableRobots {
    private PersistableRobots() { throw new IllegalAccessError(); }

    public static void save(PersistableRobot pr, OutputStream os) throws IOException {
        try (final OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
            final JsonGenerator json = Json.createGenerator(writer);
            json.writeStartObject()
                    .writeStartObject("meta")
                        .write("version", 1)
                        .writeEnd()
                    .write("name", pr.getBaseName())
                    .writeStartObject("source")
                        .write("language", pr.getSourceCodeLanguage())
                        .write("code", pr.getSourceCode())
                        .writeEnd()
                    .writeEnd()
                    .close();
        }
    }

    public static <T extends PersistableRobot> T load(Supplier<T> factory, InputStream is) throws IOException {
        final T pr = factory.get();
        try (final InputStreamReader reader = new InputStreamReader(is)) {
            final JsonReader json = Json.createReader(reader);
            final JsonObject obj = json.readObject();
            final JsonObject meta = obj.getJsonObject("meta");
            final int version = meta != null ? meta.getInt("version") : 0;
            pr.setBaseName(obj.getString("name"));
            if (version == 0) {
                pr.setSourceCode(obj.getString("source"));
            } else {
                final JsonObject source = obj.getJsonObject("source");
                pr.setSourceCode(source.getString("code"));
                pr.setSourceCodeLanguage(source.getString("language"));
            }
        }
        return pr;
    }
}
