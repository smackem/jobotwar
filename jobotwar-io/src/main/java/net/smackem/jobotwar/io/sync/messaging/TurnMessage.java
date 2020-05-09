package net.smackem.jobotwar.io.sync.messaging;

import javax.json.stream.JsonGenerator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TurnMessage extends Message {
    private final int turnId;
    private final Map<UUID, RobotStateChange> robotStateChanges;

    private TurnMessage(Builder builder) {
        this.turnId = builder.turnId;
        this.robotStateChanges = Collections.unmodifiableMap(builder.robotStateChanges);
    }

    public static class Builder {
        private final int turnId;
        private final Map<UUID, RobotStateChange> robotStateChanges = new HashMap<>();

        public Builder(int turnId) {
            this.turnId = turnId;
        }

        public Builder addRobotStateChange(UUID robotId, RobotStateChange robotStateChange) {
            this.robotStateChanges.put(robotId, robotStateChange);
            return this;
        }

        public TurnMessage build() {
            return new TurnMessage(this);
        }
    }

    public Map<UUID, RobotStateChange> robotStateChanges() {
        return robotStateChanges;
    }

    public int turnId() {
        return turnId;
    }

    @Override
    void encode(JsonGenerator json) {
        json.writeStartObject("turn")
                .write("turnid", this.turnId)
                .writeStartObject("robots");
        for (final var entry : this.robotStateChanges.entrySet()) {
            final RobotStateChange stateChange = entry.getValue();
            json.writeStartObject(entry.getKey().toString());
            stateChange.aim().ifPresent(v -> json.write("aim", v));
            stateChange.radar().ifPresent(v -> json.write("radar", v));
            stateChange.random().ifPresent(v -> json.write("random", v));
            stateChange.shot().ifPresent(v -> json.write("shot", v));
            stateChange.speedX().ifPresent(v -> json.write("speedX", v));
            stateChange.speedY().ifPresent(v -> json.write("speedY", v));
            json.writeEnd();
        }
        json.writeEnd();
        json.writeEnd();
    }
}
