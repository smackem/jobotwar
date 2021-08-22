package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class ExplosionVisual {
    @JsonProperty private final double x;
    @JsonProperty private final double y;
    @JsonProperty private final ExplosionVisualKind kind;

    @JsonCreator
    private ExplosionVisual() {
        this.x = 0;
        this.y = 0;
        this.kind = null;
    }

    public ExplosionVisual(double x, double y, ExplosionVisualKind kind) {
        this.x = x;
        this.y = y;
        this.kind = Objects.requireNonNull(kind);
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public ExplosionVisualKind kind() {
        return this.kind;
    }
}
