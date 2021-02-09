package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class MatchEvent {
    @JsonProperty private final long gameTimeMillis;
    @JsonProperty private final String event;

    @JsonCreator
    private MatchEvent() {
        this.gameTimeMillis = 0;
        this.event = null;
    }

    public MatchEvent(long gameTimeMillis, String event) {
        this.gameTimeMillis = gameTimeMillis;
        this.event = event;
    }

    public long gameTimeMillis() {
        return this.gameTimeMillis;
    }

    public String event() {
        return this.event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchEvent that = (MatchEvent) o;
        return gameTimeMillis == that.gameTimeMillis && Objects.equals(event, that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameTimeMillis, event);
    }
}
