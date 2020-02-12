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
                    .write("name", pr.getBaseName())
                    .write("source", pr.getSourceCode())
                    .writeEnd()
                    .close();
        }
    }

    public static PersistableRobot load(Supplier<PersistableRobot> factory, InputStream is) throws IOException {
        final PersistableRobot pr = factory.get();
        try (final InputStreamReader reader = new InputStreamReader(is)) {
            final JsonReader json = Json.createReader(reader);
            final JsonObject obj = json.readObject();
            pr.setBaseName(obj.getString("name"));
            pr.setSourceCode(obj.getString("source"));
        }
        return pr;
    }
}
