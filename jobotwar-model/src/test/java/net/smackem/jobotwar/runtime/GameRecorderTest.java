package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.runtime.simulation.SimulationEvent;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public class GameRecorderTest {

    @Test
    public void testWithSimulation() {
        for (int i = 0; i < 100; i++) {
            singleSimulation();
        }
    }

    private void singleSimulation() {
        final Duration duration = Duration.ofMinutes(5);
        final GameRecorder recorder = new GameRecorder(ThreadLocalRandom.current(),ctx -> {
            final Robot r1 = new Robot.Builder(r -> new CompiledProgram(r, RuntimeTests.createShooter(), ctx))
                    .name("robot1")
                    .build();
            final Robot r2 = new Robot.Builder(r -> new CompiledProgram(r, RuntimeTests.createDumbAss(), ctx))
                    .name("robot2")
                    .build();
            final Board board = new Board(500, 500, Arrays.asList(r1, r2));
            board.disperseRobots();
            return board;
        });
        final SimulationRunner runner = new SimulationRunner(recorder.board());
        final SimulationResult firstResult = runner.runGame(duration);

        final Board replayBoard = recorder.replay(null);
        final SimulationRunner replayRunner = new SimulationRunner(replayBoard);
        final SimulationResult replayResult = replayRunner.runGame(duration);

        assertThat(replayResult.outcome()).isEqualTo(firstResult.outcome());
        assertThat(replayResult.eventLog()).containsExactly(
                firstResult.eventLog().toArray(new SimulationEvent[0]));
        if (replayResult.winner() == null) {
            assertThat(firstResult.winner()).isNull();
        } else {
            assertThat(firstResult.winner().name()).isEqualTo(replayResult.winner().name());
        }
        assertThat(replayResult.duration()).isEqualTo(firstResult.duration());
    }
}