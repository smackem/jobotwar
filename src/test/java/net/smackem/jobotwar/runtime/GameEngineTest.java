package net.smackem.jobotwar.runtime;

import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

public class GameEngineTest {

    @Test
    public void movement() {
        final double acceleration = 1;
        final Robot robot = new Robot(acceleration, 0, new RuntimeProgram(
                RuntimeProgram.instruction(null, r -> {
                    r.setSpeedX(4); return null;
                })));
        robot.setX(30);
        robot.setY(30);
        final Board board = new Board(100, 100, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);

        assertThat(engine.tick().explodedProjectiles).hasSize(0);
        assertThat(robot.getActualSpeedX()).isEqualTo(1);
        assertThat(robot.getX()).isEqualTo(31);

        engine.tick();
        assertThat(robot.getActualSpeedX()).isEqualTo(2);
        assertThat(robot.getX()).isEqualTo(33);

        engine.tick();
        assertThat(robot.getActualSpeedX()).isEqualTo(3);
        assertThat(robot.getX()).isEqualTo(36);

        engine.tick();
        assertThat(robot.getActualSpeedX()).isEqualTo(4);
        assertThat(robot.getX()).isEqualTo(40);
    }

    @Test
    public void shot() {
        final Robot robot = new Robot(1, 0, new RuntimeProgram(
                RuntimeProgram.instruction(null, r -> {
                    r.setAimAngle(0); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setShot(10); return null;
                })
        ));
        robot.setX(30);
        robot.setY(30);
        final Board board = new Board(100, 100, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);

        assertThat(engine.tick().explodedProjectiles).hasSize(0);
        assertThat(board.projectiles()).hasSize(0);

        assertThat(engine.tick().explodedProjectiles).hasSize(0);
        assertThat(board.projectiles()).hasSize(1);
        final Projectile projectile = board.projectiles().iterator().next();
        assertThat(projectile.getDestination()).isEqualTo(new Vector(40, 30));
        assertThat(projectile.getPosition()).isEqualTo(new Vector(30 + projectile.getSpeed(), 30));

        int count = 0;
        Collection<Projectile> explodedProjectiles;
        do {
            explodedProjectiles = engine.tick().explodedProjectiles;
            count++;
            assertThat(count).isLessThan(100); // make sure it ends!
        } while (explodedProjectiles.size() == 0);
        assertThat(explodedProjectiles).containsOnly(projectile);
        assertThat(projectile.getPosition()).isEqualTo(new Vector(40, 30));
    }

    @Test
    public void shotAngle() {
        final Robot robot = new Robot(1, 0, new RuntimeProgram(
                RuntimeProgram.instruction(null, r -> {
                    r.setAimAngle(90); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setShot(50); return null;
                })
        ));
        robot.setX(30);
        robot.setY(30);
        final Board board = new Board(100, 100, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);

        assertThat(engine.tick().explodedProjectiles).hasSize(0);
        assertThat(board.projectiles()).hasSize(0);

        assertThat(engine.tick().explodedProjectiles).hasSize(0);
        assertThat(board.projectiles()).hasSize(1);
        final Projectile projectile = board.projectiles().iterator().next();
        assertThat(projectile.getDestination())
                .usingComparator(Vector.PROXIMITY_COMPARATOR)
                .isEqualTo(new Vector(30, 80));
        assertThat(projectile.getPosition())
                .usingComparator(Vector.PROXIMITY_COMPARATOR)
                .isEqualTo(new Vector(robot.getX(), robot.getY() + projectile.getSpeed()));

        int count = 0;
        Collection<Projectile> explodedProjectiles;
        do {
            explodedProjectiles = engine.tick().explodedProjectiles;
            count++;
            assertThat(count).isLessThan(100); // make sure it ends!
        } while (explodedProjectiles.size() == 0);
        assertThat(explodedProjectiles).containsOnly(projectile);
        assertThat(projectile.getPosition())
                .usingComparator(Vector.PROXIMITY_COMPARATOR)
                .isEqualTo(new Vector(30, 80));
    }
}