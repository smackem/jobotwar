package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.util.Arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * The game board.
 */
public class Board {
    private final int width;
    private final int height;
    private final Collection<Robot> robots;
    private final Collection<Projectile> projectiles = new ArrayList<>();

    /**
     * Initializes a new instance of {@link Board}.
     * @param width The width of the board in pixels.
     * @param height The height of the board in pixels.
     * @param robots The {@link Robot}s on the board.
     */
    public Board(int width, int height, Collection<Robot> robots) {
        this.width = Arguments.requireRange(width, 0, Constants.MAX_BOARD_WIDTH);
        this.height = Arguments.requireRange(height, 0, Constants.MAX_BOARD_HEIGHT);
        this.robots = new ArrayList<>(Objects.requireNonNull(robots));
    }

    /**
     * @return The width of the board in pixels.
     */
    public int width() {
        return this.width;
    }

    /**
     * @return The height of the board in pixels.
     */
    public int height() {
        return this.height;
    }

    /**
     * @return An modifiable collection of {@link Robot}s on the board.
     */
    public Collection<Robot> robots() {
        return this.robots;
    }

    /**
     * @return A modifiable collection containing the {@link Projectile}s on the board.
     */
    public Collection<Projectile> projectiles() {
        return this.projectiles;
    }

    /**
     * Distributes the robots on the board randomly.
     */
    public void disperseRobots() {
        final Random random = ThreadLocalRandom.current();
        for (final Robot robot : this.robots) {
            do {
                robot.setPosition(
                        Constants.ROBOT_RADIUS + random.nextDouble() * (this.width - Constants.ROBOT_RADIUS * 2),
                        Constants.ROBOT_RADIUS + random.nextDouble() * (this.height - Constants.ROBOT_RADIUS * 2));
            } while (getCloseRobot(robot) != null);
        }
    }

    /**
     * Creates a new {@link Board} with the same dimensions and {@link Robot}s that
     * have the same programs as the ones on the specified template board.
     * @param template The template board.
     * @param ctx The new {@link RobotProgramContext}.
     * @return A new {@link Board}.
     */
    public static Board fromTemplate(Board template, RobotProgramContext ctx) {
        final Collection<Robot> newRobots = template.robots().stream()
                .map(r -> Robots.fromTemplate(r, ctx))
                .collect(Collectors.toList());
        return new Board(template.width(), template.height(), newRobots);
    }

    private Robot getCloseRobot(Robot robot) {
        return this.robots.stream()
                .filter(r -> r != robot)
                .filter(r -> Vector.distance(r.position(), robot.position()) < Constants.ROBOT_RADIUS * 2)
                .findFirst()
                .orElse(null);
    }
}
