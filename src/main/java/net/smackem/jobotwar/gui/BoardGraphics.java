package net.smackem.jobotwar.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import net.smackem.jobotwar.runtime.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class BoardGraphics {
    private final Board board;
    private final Collection<Explosion> explosions = new ArrayList<>();
    private final Collection<RenderedRadarBeam> radarBeams = new ArrayList<>();

    public BoardGraphics(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    public void render(GraphicsContext gc) {
        gc.clearRect(0,0,this.board.getWidth(), this.board.getHeight());

        // radar beams
        for (final RenderedRadarBeam b : this.radarBeams) {
            final Robot robot = b.beam.getSourceRobot();
            final Paint paint = getRobotPaint(robot, b.opacity);
            final Vector hitPos = b.beam.getHitPosition();
            gc.setLineWidth(1.0);
            gc.setStroke(paint);
            gc.beginPath();
            gc.moveTo(robot.getX(), robot.getY());
            gc.lineTo(hitPos.getX(), hitPos.getY());
            gc.stroke();
            b.opacity -= 0.1;
        }
        this.radarBeams.removeIf(beam -> beam.opacity <= 0.3);

        // projectiles
        gc.setFill(Color.WHITE);
        for (final Projectile projectile : this.board.projectiles()) {
            final Vector position = projectile.getPosition();
            gc.fillOval(
                    position.getX() - 2,
                    position.getY() - 2,
                    4,
                    4);
        }

        // robots
        for (final Robot robot : this.board.getRobots()) {
            final int rgb = robot.getRgb();
            final double opacity = robot.isDead() ? 0.25 : 1.0;
            final Paint color = getRobotPaint(robot, opacity);
            gc.setFill(color);
            gc.fillOval(
                    robot.getX() - Constants.ROBOT_RADIUS,
                    robot.getY() - Constants.ROBOT_RADIUS,
                    Constants.ROBOT_RADIUS * 2,
                    Constants.ROBOT_RADIUS * 2);
        }

        // explosions
        gc.setLineWidth(5.0);
        for (final Explosion explosion : this.explosions) {
            final Paint paint = Color.rgb(0xff,0x40,0x40,1.0 - explosion.radius / (Constants.EXPLOSION_RADIUS * 1.5));
            gc.setStroke(paint);
            gc.strokeOval(
                    explosion.position.getX() - explosion.radius / 2,
                    explosion.position.getY() - explosion.radius / 2,
                    explosion.radius,
                    explosion.radius);
            explosion.radius += 2.5;
        }
        this.explosions.removeIf(e -> e.radius > Constants.EXPLOSION_RADIUS);
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

    private static Paint getRobotPaint(Robot robot, double opacity) {
        final int rgb = robot.getRgb();
        return Color.rgb(rgb >> 16 & 0xff, rgb >> 8 & 0xff, rgb & 0xff, opacity);
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
            opacity = 0.7;
        }
    }
}
