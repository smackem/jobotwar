package net.smackem.jobotwar.runtime;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class BoardTest {

    @Test
    public void disperseRobots() {
        final Robot r1 = new Robot.Builder(r ->
                new CompiledProgram(r, RuntimeTests.createDumbAss(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        final Robot r2 = new Robot.Builder(r ->
                new CompiledProgram(r, RuntimeTests.createShooter(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        final Robot r3 = new Robot.Builder(r ->
                new CompiledProgram(r, RuntimeTests.createShooter(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        final Robot r4 = new Robot.Builder(r ->
                new CompiledProgram(r, RuntimeTests.createShooter(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        final Robot r5 = new Robot.Builder(r ->
                new CompiledProgram(r, RuntimeTests.createShooter(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        final Board board = new Board(300, 300, Arrays.asList(r1, r2, r3, r4, r5));
        for (int i = 0; i < 100; i++) {
            board.disperseRobots();
            final Collection<Vector> positions = board.robots().stream()
                    .map(Robot::position)
                    .distinct()
                    .collect(Collectors.toList());
            Assertions.assertThat(positions).hasSameSizeAs(board.robots());
        }
    }

    @Test
    public void fromTemplate() {
        final Robot r1 = new Robot.Builder(r ->
                new CompiledProgram(r, RuntimeTests.createDumbAss(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        final Robot r2 = new Robot.Builder(r ->
                new CompiledProgram(r, RuntimeTests.createShooter(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        final Board template = new Board(200, 200, Arrays.asList(r1, r2));
        final Board board = Board.fromTemplate(template, null);
        assertThat(board.width()).isEqualTo(template.width());
        assertThat(board.height()).isEqualTo(template.height());
        assertThat(board.robots()).hasSameSizeAs(template.robots());
        final Iterator<Robot> iter1 = board.robots().iterator(), iter2 = template.robots().iterator();
        while (iter1.hasNext() && iter2.hasNext()) {
            RuntimeTests.assertRobotsBuiltEqual(iter1.next(), iter2.next());
        }
        assertThat(iter1.hasNext()).isFalse();
        assertThat(iter2.hasNext()).isFalse();
    }
}