package net.smackem.jobotwar.io.sync.messaging;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SyncMessage extends Message {
    private final int turnId;
    private final Map<String, RobotStateChange> robotStateChanges;

    private SyncMessage(Builder builder) {
        this.turnId = builder.turnId;
        this.robotStateChanges = Collections.unmodifiableMap(builder.robotStateChanges);
    }

    public static class Builder {
        private final int turnId;
        private final Map<String, RobotStateChange> robotStateChanges = new HashMap<>();

        public Builder(int turnId) {
            this.turnId = turnId;
        }

        public Builder addRobotStateChange(String robotName, RobotStateChange robotStateChange) {
            this.robotStateChanges.put(robotName, robotStateChange);
            return this;
        }
    }

    public Map<String, RobotStateChange> robotStateChanges() {
        return robotStateChanges;
    }

    public int turnId() {
        return turnId;
    }
}
