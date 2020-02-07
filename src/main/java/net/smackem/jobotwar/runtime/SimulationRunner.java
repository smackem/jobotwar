package net.smackem.jobotwar.runtime;

import java.time.Duration;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SimulationRunner {

    private final Board board;
    private final GameEngine engine;

    public SimulationRunner(Board board) {
        this.board = Objects.requireNonNull(board);
        this.engine = new GameEngine(board);
        if (board.robots().size() < 2) {
            throw new IllegalArgumentException("The board must contain at least two robots!");
        }
    }

    public Board board() {
        return this.board;
    }

    /**
     * Simulates a game and returns the winner. Simulation means running the game unrestricted by
     * frame rate. Frames (aka ticks) are calculated in a tight loop, so simulation is much faster.
     * @param maxDuration The maximum simulated duration in game time (as if run based upon the
     *                    standard frame rate {@link Constants#TICK_DURATION}).
     * @return A {@link SimulationResult} with the winning {@link Robot} or {@code null} if no winner could be determined.
     *         This may be the case when both robots just sit or don't hit each other within the
     *         specified {@code maxDuration}.
     */
    public SimulationResult runGame(Duration maxDuration) {
        final long maxMillis = Objects.requireNonNull(maxDuration).toMillis();
        final long tickMillis = Constants.TICK_DURATION.toMillis();
        GameEngine.TickResult result;
        long millis = 0;

        do {
            try {
                result = this.engine.tick();
            } catch (RobotProgramException e) {
                return new SimulationResult(null, Duration.ofMillis(millis));
            }
            millis += tickMillis;
        } while (result.hasEnded() == false && millis < maxMillis);

        return new SimulationResult(result.winner(), Duration.ofMillis(millis));
    }

    public static Collection<BatchSimulationResult> runBatchParallel(Board templateBoard,
                                                                     int batchSize,
                                                                     Random random,
                                                                     Duration maxDuration) {
        return IntStream.rangeClosed(1, batchSize)
                .parallel()
                .mapToObj(matchNumber -> {
                    final GameRecorder recorder = new GameRecorder(random, ctx -> {
                        final Board board = Board.fromTemplate(templateBoard, ctx);
                        board.disperseRobots();
                        return board;
                    });
                    final SimulationRunner runner = new SimulationRunner(recorder.board());
                    final SimulationResult result = runner.runGame(maxDuration);
                    return new BatchSimulationResult(result.winner(), result.duration(), matchNumber, recorder);
                })
                .collect(Collectors.toList());
    }
}
