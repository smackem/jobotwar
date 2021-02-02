package net.smackem.jobotwar.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

class GameEventDto {
    @JsonProperty private final int gameTimeMillis;
    @JsonProperty private final String event;

    @JsonCreator
    private GameEventDto() {
        this.gameTimeMillis = 0;
        this.event = null;
    }

    public GameEventDto(int gameTimeMillis, String event) {
        this.gameTimeMillis = gameTimeMillis;
        this.event = event;
    }

    public int gameTimeMillis() {
        return this.gameTimeMillis;
    }

    public String event() {
        return this.event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameEventDto that = (GameEventDto) o;
        return gameTimeMillis == that.gameTimeMillis && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameTimeMillis, event);
    }
}
