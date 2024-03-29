package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.runtime.Constants;
import net.smackem.jobotwar.util.Arguments;

import java.util.Objects;

public class RobotVisual {
    @JsonProperty private final double x;
    @JsonProperty private final double y;
    @JsonProperty private final String name;
    @JsonProperty private final int health;
    @JsonProperty private final double aimAngle;

    @JsonCreator
    private RobotVisual() {
        this.x = 0;
        this.y = 0;
        this.name = null;
        this.health = 0;
        this.aimAngle = 0;
    }

    public RobotVisual(double x, double y, String name, int health, double aimAngle) {
        this.x = x;
        this.y = y;
        this.name = Objects.requireNonNull(name);
        this.health = Arguments.requireRange(health, 0, Constants.MAX_HEALTH);
        this.aimAngle = aimAngle;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public String name() {
        return this.name;
    }

    public int health() {
        return this.health;
    }

    public double aimAngle() {
        return this.aimAngle;
    }
}
