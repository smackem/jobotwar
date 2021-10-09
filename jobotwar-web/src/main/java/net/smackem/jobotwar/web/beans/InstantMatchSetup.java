package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class InstantMatchSetup {
    @JsonProperty private int maxDurationMillis;
    @JsonProperty private int boardWidth;
    @JsonProperty private int boardHeight;
    @JsonProperty private boolean excludeFrames;
    @JsonProperty private final Collection<InstantMatchRobot> robots = new ArrayList<>();

    public int boardWidth() {
        return this.boardWidth;
    }

    public InstantMatchSetup boardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
        return this;
    }

    public int boardHeight() {
        return this.boardHeight;
    }

    public InstantMatchSetup boardHeight(int boardHeight) {
        this.boardHeight = boardHeight;
        return this;
    }

    public Collection<InstantMatchRobot> robots() {
        return this.robots;
    }

    public InstantMatchSetup addRobots(InstantMatchRobot... robots) {
        this.robots.addAll(List.of(robots));
        return this;
    }

    public Duration maxDuration() {
        return Duration.ofMillis(this.maxDurationMillis);
    }

    public InstantMatchSetup maxDuration(Duration maxDurationMillis) {
        this.maxDurationMillis = (int) maxDurationMillis.toMillis();
        return this;
    }

    @JsonIgnore
    public boolean isFramesExcluded() {
        return this.excludeFrames;
    }

    public InstantMatchSetup excludeFrames(boolean value) {
        this.excludeFrames = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstantMatchSetup that = (InstantMatchSetup) o;
        return maxDurationMillis == that.maxDurationMillis && boardWidth == that.boardWidth && boardHeight == that.boardHeight && Objects.equals(robots, that.robots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxDurationMillis, boardWidth, boardHeight, robots);
    }
}
