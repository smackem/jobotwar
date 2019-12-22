package net.smackem.jobotwar.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public final class GameEngine {
    private final Board board;

    public GameEngine(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    public void tick() {
        for (final Robot robot : this.board.getRobots()) {
            tickRobot(robot);
        }
        final Collection<Projectile> projectilesToRemove = new ArrayList<>();
        for (final Projectile projectile : this.board.projectiles()) {
            final Vector position = projectile.incrementPosition();
            if (Vector.distance(position, projectile.getDestination()) < projectile.getSpeed() / 2) {
                projectilesToRemove.add(projectile);
            }
        }
        this.board.projectiles().removeAll(projectilesToRemove);
    }

    private void tickRobot(Robot robot) {
        // movement
        //
        robot.accelerate();
        robot.setX(robot.getX() + robot.getActualSpeedX());
        robot.setY(robot.getY() + robot.getActualSpeedY());

        // shot
        //
        final int shot = robot.getShot();
        if (shot > 0) {
            final double angle = Math.toRadians(robot.getAimAngle());
            final Vector position = new Vector(robot.getX(), robot.getY());
            final Vector dest = new Vector(Math.cos(angle) * shot, Math.sin(angle) * shot);
            final Projectile projectile = new Projectile(robot, position.add(dest), Constants.PROJECTILE_SPEED);
            this.board.projectiles().add(projectile);
            robot.setShot(0);
        }
    }
}
