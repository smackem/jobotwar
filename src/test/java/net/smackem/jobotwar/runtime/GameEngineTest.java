package net.smackem.jobotwar.runtime;

import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

public class GameEngineTest {

    @Test
    public void tick() {
        final Robot robot = new Robot(1, new RobotProgram(
                r -> r.setSpeedX(4)));
        final Board board = new Board(100, 100, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);
        engine.tick();
    }
}