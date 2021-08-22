package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class RobotVisual {
    @JsonProperty private final double x;
    @JsonProperty private final double y;
    @JsonProperty private final String name;
    @JsonProperty private final String color;

    @JsonCreator
    private RobotVisual() {
        this.x = 0;
        this.y = 0;
        this.name = null;
        this.color = null;
    }

    public RobotVisual(double x, double y, String name, String color) {
        this.x = x;
        this.y = y;
        this.name = Objects.requireNonNull(name);
        this.color = Objects.requireNonNull(color);
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

    public String color() {
        return this.color;
    }
}
