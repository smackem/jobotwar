package net.smackem.jobotwar.io.sync.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public abstract class Message {

    private static final Logger log = LoggerFactory.getLogger(Message.class);

    public final void encode(OutputStream os) {
        final JsonGenerator json = Json.createGenerator(os)
                .writeStartObject();
        encode(json);
        json.writeEnd().close();
    }

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
                default -> log.warn("ignoring unknown message key '{}'", entry.getKey());
            }
        }
        return null;
    }

    abstract void encode(JsonGenerator json);

    private static Message decodeInitMessage(JsonObject message) {
        final var builder = new InitMessage.Builder();
        final var robots = message.getJsonObject("robots");
        for (final var entry : robots.entrySet()) {
            final var robotId = UUID.fromString(entry.getKey());
            final var robotJson = entry.getValue().asJsonObject();
            final var robotBuilder = new RobotInfo.Builder();
            for (final var robotEntry : robotJson.entrySet()) {
                final var value = robotEntry.getValue();
                switch (robotEntry.getKey()) {
                    case "name" -> robotBuilder.name(value.toString());
                    case "color" -> robotBuilder.color(value.toString());
                    case "x" -> robotBuilder.x(((JsonNumber)value).doubleValue());
                    case "y" -> robotBuilder.y(((JsonNumber)value).doubleValue());
                    default -> log.warn("ignoring unknown robot info key '{}'", entry.getKey());
                }
            }
            builder.addRobot(robotId, robotBuilder.build());
        }
        return builder.build();
    }

    private static Message decodeGameMessage(JsonObject message) {
        final String status = message.getString("status");
        return new GameMessage(GameStatus.valueOf(status));
    }

    private static Message decodeTurnMessage(JsonObject message) {
        final var builder = new TurnMessage.Builder(message.getInt("turnid"));
        final var robots = message.getJsonObject("robots");
        for (final var entry : robots.entrySet()) {
            final var robotId = UUID.fromString(entry.getKey());
            final var robotJson = entry.getValue().asJsonObject();
            final var robotBuilder = new RobotStateChange.Builder();
            for (final var robotEntry : robotJson.entrySet()) {
                final var value = robotEntry.getValue();
                switch (robotEntry.getKey()) {
                    case "aim" -> robotBuilder.aim(((JsonNumber)value).doubleValue());
                    case "radar" -> robotBuilder.radar(((JsonNumber)value).doubleValue());
                    case "random" -> robotBuilder.random(((JsonNumber)value).doubleValue());
                    case "shot" -> robotBuilder.shot(((JsonNumber)value).doubleValue());
                    case "speedX" -> robotBuilder.speedX(((JsonNumber)value).doubleValue());
                    case "speedY" -> robotBuilder.speedY(((JsonNumber)value).doubleValue());
                    default -> log.warn("ignoring unknown robot state change key '{}'", entry.getKey());
                }
            }
            builder.addRobotStateChange(robotId, robotBuilder.build());
        }
        return builder.build();
    }
}
