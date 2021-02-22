package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RobotWinStats extends FreezableBean {
    @JsonProperty private final String robotId;

    @JsonProperty private final String robotName;
    @JsonProperty private int playCount;
    @JsonProperty private int winCount;
    @JsonProperty private double winPercent;

    @JsonCreator
    private RobotWinStats() {
        this.robotId = null;
        this.robotName = null;
    }

    public RobotWinStats(String robotId, String robotName) {
        this.robotId = Objects.requireNonNull(robotId);
        this.robotName = Objects.requireNonNull(robotName);
    }

    public String robotId() {
        return this.robotId;
    }

    public String robotName() {
        return this.robotName;
    }

    public int playCount() {
        return this.playCount;
    }

    public RobotWinStats playCount(int playCount) {
        assertMutable();
        this.playCount = playCount;
        return this;
    }

    public int winCount() {
        return this.winCount;
    }

    public RobotWinStats winCount(int winCount) {
        assertMutable();
        this.winCount = winCount;
        return this;
    }

    public double winPercent() {
        return this.winPercent;
    }

    public RobotWinStats winPercent(double winPercent) {
        assertMutable();
        this.winPercent = winPercent;
        return this;
    }
}
