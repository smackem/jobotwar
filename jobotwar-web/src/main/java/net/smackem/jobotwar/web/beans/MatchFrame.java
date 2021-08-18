package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;

public class MatchFrame {
    @JsonProperty private final long gameMillis;
    @JsonProperty private final Collection<RobotVisual> robots = new ArrayList<>();
    @JsonProperty private final Collection<ProjectileVisual> projectiles = new ArrayList<>();
    @JsonProperty private final Collection<ExplosionVisual> explosions = new ArrayList<>();
    @JsonProperty private final Collection<RadarBeamVisual> radarBeams = new ArrayList<>();

    @JsonCreator
    private MatchFrame() {
        this.gameMillis = 0;
    }
}
