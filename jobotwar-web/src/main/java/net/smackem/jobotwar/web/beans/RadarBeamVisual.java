package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.runtime.RadarBeamHitKind;

import java.util.Objects;

public class RadarBeamVisual {
    @JsonProperty private final String emittingRobotName;
    @JsonProperty private final double hitX;
    @JsonProperty private final double hitY;
    @JsonProperty private final RadarBeamHitKind hitKind;

    @JsonCreator
    private RadarBeamVisual() {
        this.emittingRobotName = null;
        this.hitX = 0;
        this.hitY = 0;
        this.hitKind = null;
    }

    public RadarBeamVisual(String emittingRobotName, double hitX, double hitY, RadarBeamHitKind hitKind) {
        this.emittingRobotName = emittingRobotName;
        this.hitX = hitX;
        this.hitY = hitY;
        this.hitKind = Objects.requireNonNull(hitKind);
    }

    public String emittingRobotName() {
        return this.emittingRobotName;
    }

    public double hitX() {
        return this.hitX;
    }

    public double hitY() {
        return this.hitY;
    }

    public RadarBeamHitKind hitKind() {
        return this.hitKind;
    }
}
