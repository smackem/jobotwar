package net.smackem.jobotwar.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

class PlayRequest {
    @JsonProperty private int maxDurationMillis;
    @JsonProperty private int boardWidth;
    @JsonProperty private int boardHeight;
    @JsonProperty private final Collection<RobotDto> robots = new ArrayList<>();

    public int boardWidth() {
        return this.boardWidth;
    }

    public PlayRequest boardWidth(int boardWidth) {
        this.boardWidth = boardWidth;
        return this;
    }

    public int boardHeight() {
        return this.boardHeight;
    }

    public PlayRequest boardHeight(int boardHeight) {
        this.boardHeight = boardHeight;
        return this;
    }

    public Collection<RobotDto> robots() {
        return this.robots;
    }

    public PlayRequest addRobots(RobotDto... robots) {
        this.robots.addAll(List.of(robots));
        return this;
    }

    public Duration maxDuration() {
        return Duration.ofMillis(this.maxDurationMillis);
    }

    public PlayRequest maxDuration(Duration maxDurationMillis) {
        this.maxDurationMillis = (int) maxDurationMillis.toMillis();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayRequest that = (PlayRequest) o;
        return maxDurationMillis == that.maxDurationMillis && boardWidth == that.boardWidth && boardHeight == that.boardHeight && Objects.equals(robots, that.robots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxDurationMillis, boardWidth, boardHeight, robots);
    }
}
