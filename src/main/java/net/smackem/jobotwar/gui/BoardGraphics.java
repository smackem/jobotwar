package net.smackem.jobotwar.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import net.smackem.jobotwar.runtime.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class BoardGraphics {
    private final Board board;
    private final Collection<Explosion> explosions = new ArrayList<>();
    private final Collection<RenderedRadarBeam> radarBeams = new ArrayList<>();
    private static final Paint[] HEALTH_PAINT_CACHE = new Paint[101];
    private static final Paint ROBOT_CHASSIS_PAINT = Color.rgb(0x30, 0x30, 0x30);

    public BoardGraphics(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    public void render(GraphicsContext gc) {
        gc.save();
        gc.clearRect(0,0, this.board.width(), this.board.height());
        renderRadarBeams(gc);
        renderProjectiles(gc);
        renderRobots(gc);
        renderExplosions(gc);
        gc.restore();
    }

    public void addExplosions(Collection<Vector> positions) {
        for (final Vector position : positions) {
            this.explosions.add(new Explosion(position));
        }
    }

    public void addRadarBeams(Collection<RadarBeam> beams) {
        for (final RadarBeam beam : beams) {
            this.radarBeams.add(new RenderedRadarBeam(beam));
        }
    }

    private void renderRadarBeams(GraphicsContext gc) {
        gc.save();
        gc.setLineWidth(1);
        for (final RenderedRadarBeam b : this.radarBeams) {
            final Robot robot = b.beam.sourceRobot();
            final Paint paint = getRobotPaint(robot, b.opacity);
            final Vector hitPos = b.beam.hitPosition();
            gc.setStroke(paint);
            gc.strokeLine(robot.getX(), robot.getY(), hitPos.x(), hitPos.y());
            b.opacity -= 0.05;
        }
        this.radarBeams.removeIf(beam -> beam.opacity <= 0.2);
        gc.restore();
    }

    private void renderProjectiles(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        for (final Projectile projectile : this.board.projectiles()) {
            final Vector position = projectile.position();
            gc.fillOval(
                    position.x() - 3,
                    position.y() - 3,
                    6,
                    6);
        }
    }

    private void renderRobots(GraphicsContext gc) {
        final double healthBarWidth = 3;
        final double outerW = Constants.ROBOT_RADIUS * 2;
        final double innerW = outerW - healthBarWidth * 2;

        for (final Robot robot : this.board.robots()) {
            final double x = robot.getX(), y = robot.getY();
            final double outerX = x - Constants.ROBOT_RADIUS;
            final double outerY = y - Constants.ROBOT_RADIUS;
            final double innerX = outerX + healthBarWidth;
            final double innerY = outerY + healthBarWidth;
            final int health = robot.getHealth();

            // gun
            gc.save();
            gc.setStroke(ROBOT_CHASSIS_PAINT);
            gc.setLineWidth(9);
            gc.translate(x, y);
            gc.rotate(robot.getAimAngle());
            gc.translate(-x, -y);
            gc.strokeLine(x, y, x + Constants.ROBOT_RADIUS + 1, y);
            gc.restore();

            // chassis
            gc.setFill(ROBOT_CHASSIS_PAINT);
            gc.fillOval(outerX, outerY, outerW, outerW);

            // health bar
            gc.setFill(getHealthPaint(health));
            gc.fillArc(
                    outerX, outerY, outerW, outerW,
                    90,
                    health * 360.0 / 100.0,
                    ArcType.ROUND);

            // body
            final double opacity = robot.isDead() ? 0.25 : 1.0;
            final Paint color = getRobotPaint(robot, opacity);
            gc.setFill(color);
            gc.fillOval(innerX, innerY, innerW, innerW);
            gc.setLineWidth(2);
            gc.setStroke(Color.BLACK);
            gc.strokeOval(innerX, innerY, innerW, innerW);
        }
    }

    private void renderExplosions(GraphicsContext gc) {
        gc.setLineWidth(5.0);
        for (final Explosion explosion : this.explosions) {
            final Paint paint = Color.rgb(0xff,0x40,0x40,1.0 - explosion.radius / (Constants.EXPLOSION_RADIUS * 1.5));
            gc.setStroke(paint);
            gc.strokeOval(
                    explosion.position.x() - explosion.radius,
                    explosion.position.y() - explosion.radius,
                    explosion.radius * 2,
                    explosion.radius * 2);
            explosion.radius += 2.5;
        }
        this.explosions.removeIf(e -> e.radius > Constants.EXPLOSION_RADIUS);
    }

    private static Paint getRobotPaint(Robot robot, double opacity) {
        final int rgb = robot.rgb();
        return Color.rgb(rgb >> 16 & 0xff, rgb >> 8 & 0xff, rgb & 0xff, opacity);
    }

    private static Paint getHealthPaint(int health) {
        Paint cachedPaint = HEALTH_PAINT_CACHE[health];
        if (cachedPaint == null) {
            cachedPaint = Color.hsb(health * 120.0 / 100.0, 1.0, 0.75);
            HEALTH_PAINT_CACHE[health] = cachedPaint;
        }
        return cachedPaint;
    }

    private static class Explosion {
        final Vector position;
        double radius;

        Explosion(Vector position) {
            this.position = position;
            this.radius = 1;
        }
    }

    private static class RenderedRadarBeam {
        final RadarBeam beam;
        double opacity;

        RenderedRadarBeam(RadarBeam beam) {
            this.beam = beam;
            opacity = beam.hitKind() == RadarBeamHitKind.ROBOT ? 0.8 : 0.3;
        }
    }
}
