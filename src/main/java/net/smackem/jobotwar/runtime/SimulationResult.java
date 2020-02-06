package net.smackem.jobotwar.runtime;

import java.time.Duration;

public class SimulationResult {
    private final Robot winner;
    private final Duration duration;

    SimulationResult(Robot winner, Duration duration) {
        this.winner = winner;
        this.duration = duration;
    }

    public Robot winner() {
        return winner;
    }

    public Duration duration() {
        return duration;
    }
}
