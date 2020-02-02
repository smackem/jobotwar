package net.smackem.jobotwar.runtime;

import java.time.Duration;
import java.util.Objects;

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
            result = this.engine.tick();
            millis += tickMillis;
        } while (result.hasEnded() == false && millis < maxMillis);

        return new SimulationResult(result.winner(), Duration.ofMillis(millis));
    }

    public static class SimulationResult {
        private final Robot winner;
        private final Duration duration;

        private SimulationResult(Robot winner, Duration duration) {
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
}
