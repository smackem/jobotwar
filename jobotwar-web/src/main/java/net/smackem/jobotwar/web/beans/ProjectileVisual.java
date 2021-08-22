package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectileVisual {
    @JsonProperty private final double x;
    @JsonProperty private final double y;

    @JsonCreator
    private ProjectileVisual() {
        this.x = 0;
        this.y = 0;
    }

    public ProjectileVisual(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }
}
