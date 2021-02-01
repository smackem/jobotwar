package net.smackem.jobotwar.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class PlayResponse {
    @JsonProperty private final SimulationResult.Outcome outcome;
    @JsonProperty private String winner;
    @JsonProperty private int durationMillis;
    @JsonProperty private final Collection<GameEventDto> eventLog = new ArrayList<>();

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
}
