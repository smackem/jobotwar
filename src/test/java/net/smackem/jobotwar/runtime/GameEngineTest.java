package net.smackem.jobotwar.runtime;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

public class GameEngineTest {

    @Test
    public void movement() {
        final double acceleration = 1;
        final Robot robot = new Robot(acceleration, new LoopProgram(
                r -> r.setSpeedX(4)));
        final Board board = new Board(100, 100, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);

        assertThat(engine.tick()).hasSize(0);
        assertThat(robot.getActualSpeedX()).isEqualTo(1);
        assertThat(robot.getX()).isEqualTo(1);

        engine.tick();
        assertThat(robot.getActualSpeedX()).isEqualTo(2);
        assertThat(robot.getX()).isEqualTo(3);

        engine.tick();
        assertThat(robot.getActualSpeedX()).isEqualTo(3);
        assertThat(robot.getX()).isEqualTo(6);

        engine.tick();
        assertThat(robot.getActualSpeedX()).isEqualTo(4);
        assertThat(robot.getX()).isEqualTo(10);
    }

    @Test
    public void shot() {
        final Robot robot = new Robot(1, new LoopProgram(
                r -> r.setAimAngle(0),
                r -> r.setShot(10)));
        final Board board = new Board(100, 100, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);

        assertThat(engine.tick()).hasSize(0);
        assertThat(board.projectiles()).hasSize(0);

        assertThat(engine.tick()).hasSize(0);
        assertThat(board.projectiles()).hasSize(1);
        final Projectile projectile = board.projectiles().iterator().next();
        assertThat(projectile.getDestination()).isEqualTo(new Vector(10, 0));
        assertThat(projectile.getPosition()).isEqualTo(new Vector(projectile.getSpeed(), 0));

        int count = 0;
        Collection<Projectile> explodedProjectiles;
        do {
            explodedProjectiles = engine.tick();
            count++;
            assertThat(count).isLessThan(100); // make sure it ends!
        } while (explodedProjectiles.size() == 0);
        assertThat(explodedProjectiles).containsOnly(projectile);
        assertThat(projectile.getPosition()).isEqualTo(new Vector(10, 0));
    }
}