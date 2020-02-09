package net.smackem.jobotwar.runtime.simulation;

import net.smackem.jobotwar.runtime.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Runs simulation games, which differ from real-time games in that frames are executed
 * as fast as possible in a loop instead of with a fixed frame rate.
 */
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
        final long tickMillis = Constants.TICK_DURATION.toMillis();
        final long maxMillis = Objects.requireNonNull(maxDuration).toMillis();
        final Collection<SimulationEvent> eventLog = new ArrayList<>();
        GameEngine.TickResult result;
        long millis = 0;

        do {
            try {
                result = this.engine.tick();
            } catch (RobotProgramException e) {
                return new SimulationResult(SimulationResult.Outcome.ERROR, null, Duration.ofMillis(millis), eventLog);
            }

            for (final Robot r : result.killedRobots()) {
                eventLog.add(new SimulationEvent(millis, String.format("%s got killed", r.name())));
            }

            millis += tickMillis;
        } while (result.hasEnded() == false && millis < maxMillis);

        final SimulationResult.Outcome outcome = result.isDraw()
                ? SimulationResult.Outcome.DRAW
                : SimulationResult.Outcome.WIN;

        return new SimulationResult(outcome, result.winner(), Duration.ofMillis(millis), eventLog);
    }

    /**
     * Runs multiple recorded simulations in parallel.
     * @param templateBoard The board that serves as a template for the simulation matches. The template
     *                      is not mutated during the simulation.
     * @param batchSize The number of matches to run.
     * @param random The random number generator to use.
     * @param maxDuration The maximum duration of a match. If this duration is exceeded, the match counts
     *                    as draw.
     * @return The {@link BatchSimulationResult}s of the simulation, in arbitrary order.
     */
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
                    return new BatchSimulationResult(result.outcome(), result.winner(), result.duration(),
                            result.eventLog(), matchNumber, recorder);
                })
                .collect(Collectors.toList());
    }
}
