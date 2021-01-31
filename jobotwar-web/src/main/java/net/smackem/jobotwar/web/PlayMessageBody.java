package net.smackem.jobotwar.web;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class PlayMessageBody {
    @JsonProperty private double boardWidth;
    @JsonProperty private double boardHeight;
    @JsonProperty private final Collection<RobotDto> robots = new ArrayList<>();

    public double boardWidth() {
        return this.boardWidth;
    }

    public PlayMessageBody boardWidth(double boardWidth) {
        this.boardWidth = boardWidth;
        return this;
    }

    public double boardHeight() {
        return this.boardHeight;
    }

    public PlayMessageBody boardHeight(double boardHeight) {
        this.boardHeight = boardHeight;
        return this;
    }

    public Collection<RobotDto> robots() {
        return this.robots;
    }

    public PlayMessageBody robots(RobotDto... robots) {
        this.robots.addAll(List.of(robots));
        return this;
    }
}
