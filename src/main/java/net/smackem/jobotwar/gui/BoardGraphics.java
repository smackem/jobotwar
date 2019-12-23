package net.smackem.jobotwar.gui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.Constants;
import net.smackem.jobotwar.runtime.Projectile;
import net.smackem.jobotwar.runtime.Robot;

import java.util.Objects;

public class BoardGraphics {
    private final Board board;

    public BoardGraphics(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    public void render(GraphicsContext gc) {
        // robots
        for (final Robot robot : this.board.getRobots()) {
            gc.setFill(Color.BLUE);
            gc.fillOval(
                    robot.getX() - Constants.ROBOT_RADIUS,
                    robot.getY() - Constants.ROBOT_RADIUS,
                    Constants.ROBOT_RADIUS * 2,
                    Constants.ROBOT_RADIUS * 2);
        }

        // projectiles
    }
}
