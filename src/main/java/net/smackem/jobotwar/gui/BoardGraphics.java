package net.smackem.jobotwar.gui;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
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
        gc.setLineWidth(1);
        gc.setLineDashes(1, 3);
        for (final RenderedRadarBeam b : this.radarBeams) {
            final Robot robot = b.beam.getSourceRobot();
            final Paint paint = getRobotPaint(robot, b.opacity);
            final Vector hitPos = b.beam.getHitPosition();
            gc.setStroke(paint);
            gc.strokeLine(robot.getX(), robot.getY(), hitPos.getX(), hitPos.getY());
            b.opacity -= 0.05;
        }
        this.radarBeams.removeIf(beam -> beam.opacity <= 0.2);

        // projectiles
        gc.setFill(Color.WHITE);
        for (final Projectile projectile : this.board.projectiles()) {
            final Vector position = projectile.getPosition();
            gc.fillOval(
                    position.getX() - 3,
                    position.getY() - 3,
                    6,
                    6);
        }

        // robots
        gc.setLineDashes(null);
        gc.setStroke(Color.WHITE);
        final double healthBarWidth = 3;
        for (final Robot robot : this.board.getRobots()) {
            final Rectangle2D outer = new Rectangle2D(
                    robot.getX() - Constants.ROBOT_RADIUS,
                    robot.getY() - Constants.ROBOT_RADIUS,
                    Constants.ROBOT_RADIUS * 2,
                    Constants.ROBOT_RADIUS * 2);
            final Rectangle2D inner = new Rectangle2D(
                    outer.getMinX() + healthBarWidth,
                    outer.getMinY() + healthBarWidth,
                    outer.getWidth() - healthBarWidth * 2,
                    outer.getHeight() - healthBarWidth * 2);
            gc.setFill(Color.LIGHTGREEN);
            gc.fillArc(
                    outer.getMinX(), outer.getMinY(), outer.getWidth(), outer.getHeight(),
                    90,
                    robot.getHealth() * 360.0 / 100.0,
                    ArcType.ROUND);
            final int rgb = robot.getRgb();
            final double opacity = robot.isDead() ? 0.25 : 1.0;
            final Paint color = getRobotPaint(robot, opacity);
            gc.setFill(color);
            gc.fillOval(inner.getMinX(), inner.getMinY(), inner.getWidth(), inner.getHeight());
            gc.strokeOval(inner.getMinX(), inner.getMinY(), inner.getWidth(), inner.getHeight());
            gc.strokeOval(outer.getMinX(), outer.getMinY(), outer.getWidth(), outer.getHeight());
        }

        // explosions
        gc.setLineWidth(5.0);
        for (final Explosion explosion : this.explosions) {
            final Paint paint = Color.rgb(0xff,0x40,0x40,1.0 - explosion.radius / (Constants.EXPLOSION_RADIUS * 1.5));
            gc.setStroke(paint);
            gc.strokeOval(
                    explosion.position.getX() - explosion.radius,
                    explosion.position.getY() - explosion.radius,
                    explosion.radius * 2,
                    explosion.radius * 2);
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
            opacity = beam.getHitKind() == RadarBeamHitKind.ROBOT ? 1.0 : 0.4;
        }
    }
}
