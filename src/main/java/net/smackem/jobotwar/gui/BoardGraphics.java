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

    public BoardGraphics(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    public void render(GraphicsContext gc) {
        gc.clearRect(0,0,this.board.getWidth(), this.board.getHeight());

        // robots
        for (final Robot robot : this.board.getRobots()) {
            final int rgb = robot.getRgb();
            final double opacity = robot.isDead() ? 0.25 : 1.0;
            final Paint color = Color.rgb(rgb >> 16 & 0xff, rgb >> 8 & 0xff, rgb >> 0 & 0xff, opacity);
            gc.setFill(color);
            gc.fillOval(
                    robot.getX() - Constants.ROBOT_RADIUS,
                    robot.getY() - Constants.ROBOT_RADIUS,
                    Constants.ROBOT_RADIUS * 2,
                    Constants.ROBOT_RADIUS * 2);
        }

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

    private static class Explosion {
        final Vector position;
        double radius;

        Explosion(Vector position) {
            this.position = position;
            this.radius = 1;
        }
    }
}
