package net.smackem.jobotwar.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.smackem.jobotwar.runtime.*;

import java.util.Objects;

public class BoardGraphics {
    private final Board board;

    public BoardGraphics(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    public void render(GraphicsContext gc) {
        gc.clearRect(0,0,this.board.getWidth(), this.board.getHeight());

        // robots
        gc.setFill(Color.AQUA);
        for (final Robot robot : this.board.getRobots()) {
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
    }
}
