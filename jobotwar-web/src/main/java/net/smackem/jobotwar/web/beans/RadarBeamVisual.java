package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.runtime.RadarBeamHitKind;

import java.util.Objects;

public class RadarBeamVisual {
    @JsonProperty private final double x1;
    @JsonProperty private final double y1;
    @JsonProperty private final double x2;
    @JsonProperty private final double y2;
    @JsonProperty private final RadarBeamHitKind kind;

    @JsonCreator
    private RadarBeamVisual() {
        this.x1 = 0;
        this.y1 = 0;
        this.x2 = 0;
        this.y2 = 0;
        this.kind = null;
    }

    public RadarBeamVisual(double x1, double y1, double x2, double y2, RadarBeamHitKind kind) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.kind = Objects.requireNonNull(kind);
    }

    public double x1() {
        return this.x1;
    }

    public double y1() {
        return this.y1;
    }

    public double x2() {
        return this.x2;
    }

    public double y2() {
        return this.y2;
    }

    public RadarBeamHitKind kind() {
        return this.kind;
    }
}
