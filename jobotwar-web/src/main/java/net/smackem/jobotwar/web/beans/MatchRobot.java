package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class MatchRobot extends FreezableBean {
    @JsonProperty private final String robotId;
    @JsonProperty private double x;
    @JsonProperty private double y;

    @JsonCreator
    private MatchRobot() {
        this.robotId = null;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchRobot that = (MatchRobot) o;
        return Double.compare(that.x, x) == 0 && Double.compare(that.y, y) == 0 && Objects.equals(robotId, that.robotId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(robotId, x, y);
    }

    @Override
    public String toString() {
        return "MatchRobot{" +
                "robotId='" + robotId + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
