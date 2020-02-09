package net.smackem.jobotwar.runtime.simulation;

import net.smackem.jobotwar.runtime.Robot;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class SimulationResult {
    private final Outcome outcome;
    private final Robot winner;
    private final Duration duration;
    private final Collection<SimulationEvent> eventLog;

    public enum Outcome { WIN, DRAW, ERROR }

    SimulationResult(Outcome outcome, Robot winner, Duration duration, Collection<SimulationEvent> eventLog) {
        this.outcome = Objects.requireNonNull(outcome);
        this.winner = winner;
        this.duration = duration;
        this.eventLog = Objects.requireNonNull(eventLog);
    }

    public Outcome outcome() {
        return this.outcome;
    }

    public Robot winner() {
        return winner;
    }

    public Duration duration() {
        return duration;
    }

    public Collection<SimulationEvent> eventLog() {
        return Collections.unmodifiableCollection(this.eventLog);
    }
}
