package net.smackem.jobotwar.runtime;

import java.time.Duration;

public final class BatchSimulationResult extends SimulationResult {
    private final int matchNumber;
    private final GameRecorder recorder;

    public BatchSimulationResult(Robot winner, Duration duration, int matchNumber, GameRecorder recorder) {
        super(winner, duration);
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
