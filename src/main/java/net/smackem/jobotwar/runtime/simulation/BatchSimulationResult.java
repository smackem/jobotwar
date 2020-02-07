package net.smackem.jobotwar.runtime.simulation;

import net.smackem.jobotwar.runtime.GameRecorder;
import net.smackem.jobotwar.runtime.Robot;

import java.time.Duration;
import java.util.Collection;

public final class BatchSimulationResult extends SimulationResult {
    private final int matchNumber;
    private final GameRecorder recorder;

    public BatchSimulationResult(Outcome outcome, Robot winner, Duration duration, Collection<SimulationEvent> eventLog,
                                 int matchNumber, GameRecorder recorder) {
        super(outcome, winner, duration, eventLog);
        this.matchNumber = matchNumber;
        this.recorder = recorder;
    }

    public int matchNumber() {
        return this.matchNumber;
    }

    public GameRecorder recorder() {
        return this.recorder;
    }
}
