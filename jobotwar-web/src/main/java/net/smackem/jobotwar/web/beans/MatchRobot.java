package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class MatchRobot extends FreezableBean {
    @JsonProperty private final String robotId;
    @JsonProperty private double x;
    @JsonProperty private double y;

    public MatchRobot(String robotId) {
        this.robotId = Objects.requireNonNull(robotId);
    }

    public String robotId() {
        return this.robotId;
    }

    public double x() {
        return this.x;
    }

    public MatchRobot x(double x) {
        this.x = x;
        return this;
    }

    public double y() {
        return this.y;
    }

    public MatchRobot y(double y) {
        this.y = y;
        return this;
    }
}
