package net.smackem.jobotwar.web.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;

import java.time.Duration;
import java.util.*;

public class InstantMatchResult extends FreezableBean {
    @JsonProperty private final SimulationResult.Outcome outcome;
    @JsonProperty private String winner;
    @JsonProperty private int durationMillis;
    @JsonProperty private final Collection<MatchEvent> eventLog = new ArrayList<>();
    @JsonProperty private final Collection<MatchFrame> frames = new ArrayList<>();

    @JsonCreator
    private InstantMatchResult() {
        this.outcome = null;
    }

    public InstantMatchResult(SimulationResult.Outcome outcome) {
        this.outcome = outcome;
    }

    public SimulationResult.Outcome outcome() {
        return this.outcome;
    }

    public String winner() {
        return this.winner;
    }

    public InstantMatchResult winner(String winner) {
        this.winner = winner;
        return this;
    }

    public Duration duration() {
        return Duration.ofMillis(this.durationMillis);
    }

    public InstantMatchResult duration(Duration duration) {
        assertMutable();
        this.durationMillis = (int) duration.toMillis();
        return this;
    }

    public Collection<MatchEvent> eventLog() {
        return Collections.unmodifiableCollection(this.eventLog);
    }

    public InstantMatchResult addEvents(MatchEvent... events) {
        assertMutable();
        this.eventLog.addAll(List.of(events));
        return this;
    }

    public Collection<MatchFrame> frames() {
        return Collections.unmodifiableCollection(this.frames);
    }

    public InstantMatchResult addFrames(MatchFrame... frames) {
        assertMutable();
        this.frames.addAll(List.of(frames));
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstantMatchResult that = (InstantMatchResult) o;
        return durationMillis == that.durationMillis &&
               outcome == that.outcome &&
               Objects.equals(winner, that.winner) &&
               Objects.equals(eventLog, that.eventLog);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outcome, winner, durationMillis, eventLog);
    }
}
