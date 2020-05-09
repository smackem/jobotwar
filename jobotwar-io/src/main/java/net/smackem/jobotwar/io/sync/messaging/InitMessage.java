package net.smackem.jobotwar.io.sync.messaging;

import javax.json.stream.JsonGenerator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InitMessage extends Message {
    private final Map<UUID, RobotInfo> robots;

    private InitMessage(Builder builder) {
        this.robots = Collections.unmodifiableMap(builder.robots);
    }

    public static class Builder {
        private final Map<UUID, RobotInfo> robots = new HashMap<>();

        public Builder addRobot(UUID robotId, RobotInfo robotInfo) {
            this.robots.put(robotId, robotInfo);
            return this;
        }

        public InitMessage build() {
            return new InitMessage(this);
        }
    }

    public Map<UUID, RobotInfo> robots() {
        return this.robots;
    }

    @Override
    void encode(JsonGenerator json) {
        json.writeStartObject("init");
        json.writeStartObject("robots");
        for (final var entry : this.robots.entrySet()) {
            final RobotInfo robot = entry.getValue();
            json.writeStartObject(entry.getKey().toString());
            robot.name().ifPresent(v -> json.write("name", v));
            robot.color().ifPresent(v -> json.write("color", v));
            robot.x().ifPresent(v -> json.write("x", v));
            robot.y().ifPresent(v -> json.write("y", v));
            robot.isReady().ifPresent(v -> json.write("ready", v));
            json.writeEnd();
        }
        json.writeEnd();
        json.writeEnd();
    }
}
