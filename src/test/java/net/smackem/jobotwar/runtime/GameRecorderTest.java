package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.runtime.simulation.SimulationEvent;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class GameRecorderTest {

    private static final Random RANDOM = new Random();

    @Test
    public void testWithSimulation() {
        for (int i = 0; i < 100; i++) {
            singleSimulation();
        }
    }

    private void singleSimulation() {
        final Duration duration = Duration.ofMinutes(5);
        final GameRecorder recorder = new GameRecorder(RANDOM, ctx -> {
            final Robot r1 = new Robot.Builder(r -> new CompiledProgram(r, createShooter(), ctx))
                    .name("robot1")
                    .build();
            final Robot r2 = new Robot.Builder(r -> new CompiledProgram(r, createDumbAss(), ctx))
                    .name("robot2")
                    .build();
            final Board board = new Board(500, 500, Arrays.asList(r1, r2));
            board.disperseRobots();
            return board;
        });
        final SimulationRunner runner = new SimulationRunner(recorder.board());
        final SimulationResult firstResult = runner.runGame(duration);

        final Board replayBoard = recorder.replay();
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

    private static Program createShooter() {
        final String source = "" +
                "loop:\n" +
                "    AIM + 7 -> AIM -> RADAR\n" +
                "    goto loop unless RADAR < 0\n" +
                "shoot:\n" +
                "    0 - RADAR -> SHOT\n" +
                "    AIM -> RADAR\n" +
                "    goto shoot if RADAR < 0 or RANDOM < 0.5\n" +
                "    goto loop\n";
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source);
        assert result.hasErrors() == false;
        return result.program();
    }

    private static Program createDumbAss() {
        final String source = "" +
                "loop:\n" +
                "    AIM + 3 -> AIM\n" +
                "    1000 -> SHOT\n" +
                "    50 - RANDOM * 100 -> SPEEDX if AIM % 45 = 0\n" +
                "    goto loop\n";
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source);
        assert result.hasErrors() == false;
        return result.program();
    }
}