package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MatchFrame extends FreezableBean {
    @JsonProperty private final long gameMillis;
    @JsonProperty private final Collection<RobotVisual> robots = new ArrayList<>();
    @JsonProperty private final Collection<ProjectileVisual> projectiles = new ArrayList<>();
    @JsonProperty private final Collection<ExplosionVisual> explosions = new ArrayList<>();
    @JsonProperty private final Collection<RadarBeamVisual> radarBeams = new ArrayList<>();

    @JsonCreator
    private MatchFrame() {
        this.gameMillis = 0;
    }

    public MatchFrame(long gameMillis) {
        this.gameMillis = gameMillis;
    }

    public long gameMillis() {
        return this.gameMillis;
    }

    public Collection<RobotVisual> robots() {
        return Collections.unmodifiableCollection(this.robots);
    }

    public MatchFrame addRobots(RobotVisual... robots) {
        assertMutable();
        this.robots.addAll(List.of(robots));
        return this;
    }

    public Collection<ProjectileVisual> projectiles() {
        return Collections.unmodifiableCollection(this.projectiles);
    }

    public MatchFrame addProjectiles(ProjectileVisual... projectiles) {
        assertMutable();
        this.projectiles.addAll(List.of(projectiles));
        return this;
    }

    public Collection<ExplosionVisual> explosions() {
        return Collections.unmodifiableCollection(this.explosions);
    }

    public MatchFrame addExplosions(ExplosionVisual... explosions) {
        assertMutable();
        this.explosions.addAll(List.of(explosions));
        return this;
    }

    public Collection<RadarBeamVisual> radarBeams() {
        return Collections.unmodifiableCollection(this.radarBeams);
    }

    public MatchFrame addRadarBeams(RadarBeamVisual... radarBeams) {
        assertMutable();
        this.radarBeams.addAll(List.of(radarBeams));
        return this;
    }
}
