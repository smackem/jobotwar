package net.smackem.jobotwar.runtime;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.*;

public class GameEngineTest {

    @Test
    public void movement() {
        final Robot robot = new Robot.Builder(
            rob -> new RuntimeProgram(rob,
                RuntimeProgram.instruction(null, r -> {
                    r.setSpeedX(4); return null;
                }))
        ).acceleration(1).x(30).y(30).build();
        final Board board = new Board(100, 100, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);

        assertThat(engine.tick().explodedProjectiles()).hasSize(0);
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
        final Robot robot = new Robot.Builder(
            rob -> new RuntimeProgram(rob,
                RuntimeProgram.instruction(null, r -> {
                    r.setAimAngle(0); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setShot(10); return null;
                })
        )).acceleration(1).x(30).y(30).build();
        final Board board = new Board(100, 100, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);

        assertThat(engine.tick().explodedProjectiles()).hasSize(0);
        assertThat(board.projectiles()).hasSize(0);

        assertThat(engine.tick().explodedProjectiles()).hasSize(0);
        assertThat(board.projectiles()).hasSize(1);
        final Projectile projectile = board.projectiles().iterator().next();
        assertThat(projectile.destination()).isEqualTo(new Vector(40, 30));
        assertThat(projectile.position()).isEqualTo(new Vector(30 + projectile.speed(), 30));

        int count = 0;
        Collection<Projectile> explodedProjectiles;
        do {
            explodedProjectiles = engine.tick().explodedProjectiles();
            count++;
            assertThat(count).isLessThan(100); // make sure it ends!
        } while (explodedProjectiles.size() == 0);
        assertThat(explodedProjectiles).containsOnly(projectile);
        assertThat(projectile.position()).isEqualTo(new Vector(40, 30));
    }

    @Test
    public void shotAngle() {
        final Robot robot = new Robot.Builder(
            rob -> new RuntimeProgram(rob,
                RuntimeProgram.instruction(null, r -> {
                    r.setAimAngle(90); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setShot(50); return null;
                })
        )).acceleration(1).x(30).y(30).build();
        final Board board = new Board(100, 100, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);
        final VectorComparator vectorComparator = new VectorComparator(VectorComparator.DEFAULT_TOLERANCE);

        assertThat(engine.tick().explodedProjectiles()).hasSize(0);
        assertThat(board.projectiles()).hasSize(0);

        assertThat(engine.tick().explodedProjectiles()).hasSize(0);
        assertThat(board.projectiles()).hasSize(1);
        final Projectile projectile = board.projectiles().iterator().next();
        assertThat(projectile.destination())
                .usingComparator(vectorComparator)
                .isEqualTo(new Vector(30, 80));
        assertThat(projectile.position())
                .usingComparator(vectorComparator)
                .isEqualTo(new Vector(robot.getX(), robot.getY() + projectile.speed()));

        int count = 0;
        Collection<Projectile> explodedProjectiles;
        do {
            explodedProjectiles = engine.tick().explodedProjectiles();
            count++;
            assertThat(count).isLessThan(100); // make sure it ends!
        } while (explodedProjectiles.size() == 0);
        assertThat(explodedProjectiles).containsOnly(projectile);
        assertThat(projectile.position())
                .usingComparator(vectorComparator)
                .isEqualTo(new Vector(30, 80));
    }

    @Test
    public void radarWall() {
        final int width = 100, height = 100;
        final Robot robot = new Robot.Builder(
            rob -> new RuntimeProgram(rob,
                RuntimeProgram.instruction(null, r -> {
                    r.setRadarAngle(0.0); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setRadarAngle(90.0); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setRadarAngle(180.0); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setRadarAngle(270.0); return null;
                }),
                RuntimeProgram.instruction(null, r -> {
                    r.setRadarAngle(45.0); return null;
                })
        )).acceleration(1).x(width / 2.0).y(height / 2.0).build();
        final Board board = new Board(width, height, Collections.singleton(robot));
        final GameEngine engine = new GameEngine(board);
        final VectorComparator vectorComparator = new VectorComparator(1);

        // 0 degrees - hit right wall
        GameEngine.TickResult result = engine.tick();
        assertThat(result.radarBeams()).hasSize(1);
        RadarBeam beam = result.radarBeams().iterator().next();
        assertThat(beam.hitKind()).isEqualTo(RadarBeamHitKind.WALL);
        assertThat(beam.sourceRobot()).isEqualTo(robot);
        assertThat(beam.hitPosition())
                .usingComparator(vectorComparator)
                .isEqualTo(new Vector(width, robot.getY()));

        // 90 degrees - hit bottom wall
        result = engine.tick();
        assertThat(result.radarBeams()).hasSize(1);
        beam = result.radarBeams().iterator().next();
        assertThat(beam.hitKind()).isEqualTo(RadarBeamHitKind.WALL);
        assertThat(beam.sourceRobot()).isEqualTo(robot);
        assertThat(beam.hitPosition())
                .usingComparator(vectorComparator)
                .isEqualTo(new Vector(robot.getX(), height));

        // 180 degrees - hit left wall
        result = engine.tick();
        assertThat(result.radarBeams()).hasSize(1);
        beam = result.radarBeams().iterator().next();
        assertThat(beam.hitKind()).isEqualTo(RadarBeamHitKind.WALL);
        assertThat(beam.sourceRobot()).isEqualTo(robot);
        assertThat(beam.hitPosition())
                .usingComparator(vectorComparator)
                .isEqualTo(new Vector(0, robot.getY()));

        // 270 degrees - hit top wall
        result = engine.tick();
        assertThat(result.radarBeams()).hasSize(1);
        beam = result.radarBeams().iterator().next();
        assertThat(beam.hitKind()).isEqualTo(RadarBeamHitKind.WALL);
        assertThat(beam.sourceRobot()).isEqualTo(robot);
        assertThat(beam.hitPosition())
                .usingComparator(vectorComparator)
                .isEqualTo(new Vector(robot.getX(), 0));

        // 45 degrees - hit bottom right corner
        result = engine.tick();
        assertThat(result.radarBeams()).hasSize(1);
        beam = result.radarBeams().iterator().next();
        assertThat(beam.hitKind()).isEqualTo(RadarBeamHitKind.WALL);
        assertThat(beam.sourceRobot()).isEqualTo(robot);
        assertThat(beam.hitPosition())
                .usingComparator(vectorComparator)
                .isEqualTo(new Vector(width, height));
    }

    @Test
    public void radarRobot() {
        final Robot robot1 = new Robot.Builder(
            rob -> new RuntimeProgram(rob,
                RuntimeProgram.instruction(null, r -> {
                    r.setRadarAngle(180.0); return null;
                })
        )).x(50).y(50).build();

        final Robot robot2 = new Robot.Builder(
                rob -> new RuntimeProgram(rob)).x(20).y(50).build();

        final Board board = new Board(100, 100, Arrays.asList(robot1, robot2));
        final GameEngine engine = new GameEngine(board);
        final VectorComparator vectorComparator = new VectorComparator(1);

        GameEngine.TickResult result = engine.tick();
        assertThat(result.radarBeams()).hasSize(1);
        RadarBeam beam = result.radarBeams().iterator().next();
        assertThat(beam.hitKind()).isEqualTo(RadarBeamHitKind.ROBOT);
        assertThat(beam.sourceRobot()).isEqualTo(robot1);
        assertThat(beam.hitPosition())
                .usingComparator(vectorComparator)
                .isEqualTo(robot2.getPosition());
    }
}