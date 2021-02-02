package net.smackem.jobotwar.web;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;

import java.time.Duration;
import java.util.*;

class PlayResponse {
    @JsonProperty private final SimulationResult.Outcome outcome;
    @JsonProperty private String winner;
    @JsonProperty private int durationMillis;
    @JsonProperty private final Collection<GameEventDto> eventLog = new ArrayList<>();

    @JsonCreator
    private PlayResponse() {
        this.outcome = null;
    }

    public PlayResponse(SimulationResult.Outcome outcome) {
        this.outcome = outcome;
    }

    public SimulationResult.Outcome outcome() {
        return this.outcome;
    }

    public String winner() {
        return this.winner;
    }

    public PlayResponse winner(String winner) {
        this.winner = winner;
        return this;
    }

    public Duration duration() {
        return Duration.ofMillis(this.durationMillis);
    }

    public PlayResponse duration(Duration duration) {
        this.durationMillis = (int) duration.toMillis();
        return this;
    }

    public Collection<GameEventDto> eventLog() {
        return Collections.unmodifiableCollection(this.eventLog);
    }

    public PlayResponse addEvents(GameEventDto... events) {
        this.eventLog.addAll(List.of(events));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayResponse that = (PlayResponse) o;
        return durationMillis == that.durationMillis && outcome == that.outcome && Objects.equals(winner, that.winner) && Objects.equals(eventLog, that.eventLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outcome, winner, durationMillis, eventLog);
    }
}
