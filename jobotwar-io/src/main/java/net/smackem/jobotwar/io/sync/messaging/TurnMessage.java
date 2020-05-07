package net.smackem.jobotwar.io.sync.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * <code>
 *     "turn": {
 *         "turnid": 1,
 *         "state": {
 *              RobotStateChange...
 *         }
 *     },
 * </code>
 */
public class TurnMessage extends Message {
    private final int turnId;
    private final Collection<RobotStateChange> robotStateChanges;

    private TurnMessage(Builder builder) {
        this.turnId = builder.turnId;
        this.robotStateChanges = Collections.unmodifiableCollection(builder.robotStateChanges);
    }

    public static class Builder {
        private final int turnId;
        private final Collection<RobotStateChange> robotStateChanges = new ArrayList<>();

        public Builder(int turnId) {
            this.turnId = turnId;
        }

        public Builder addRobotStateChange(RobotStateChange robotStateChange) {
            this.robotStateChanges.add(robotStateChange);
            return this;
        }
    }

    public int turnId() {
        return this.turnId;
    }

    public Collection<RobotStateChange> robotStateChanges() {
        return robotStateChanges;
    }
}
