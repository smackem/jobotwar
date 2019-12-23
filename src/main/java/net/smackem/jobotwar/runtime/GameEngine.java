package net.smackem.jobotwar.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public final class GameEngine {
    private final Board board;

    public GameEngine(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    public Collection<Projectile> tick() {
        for (final Robot robot : this.board.getRobots()) {
            tickRobot(robot);
        }

        final Collection<Projectile> explodedProjectiles = new ArrayList<>();
        for (final Projectile projectile : this.board.projectiles()) {
            if (tickProjectile(projectile)) {
                explodedProjectiles.add(projectile);
            }
        }
        this.board.projectiles().removeAll(explodedProjectiles);
        return explodedProjectiles;
    }

    private boolean tickProjectile(Projectile projectile) {
        final Robot closeRobot = findCloseRobot(projectile, Constants.ROBOT_RADIUS);
        if (closeRobot != null || isProjectileExploding(projectile)) {
            explodeProjectile(projectile);
            return true;
        }
        projectile.incrementPosition();
        return false;
    }

    private boolean isProjectileExploding(Projectile projectile) {
        final Vector position = projectile.getPosition();
        final double x = position.getX();
        final double y = position.getY();

        // out of bounds?
        if (x < 0 || x >= this.board.getWidth()) {
            return true;
        }
        if (y < 0 || y >= this.board.getHeight()) {
            return true;
        }

        // at destination?
        return position.equals(projectile.getDestination());
    }

    private void explodeProjectile(Projectile projectile) {
        final Vector position = projectile.getPosition();
        for (final Robot robot : this.board.getRobots()) {
            final Vector robotPosition = new Vector(robot.getX(), robot.getY());
            if (Vector.distance(position, robotPosition) < Constants.EXPLOSION_RADIUS) {
                robot.setHealth(Math.max(0, robot.getHealth() - 30));
            }
        }
    }

    private Robot findCloseRobot(Projectile projectile, double tolerance) {
        final Vector position = projectile.getPosition();
        for (final Robot robot : this.board.getRobots()) {
            if (robot == projectile.getSourceRobot()) {
                continue;
            }
            final Vector robotPosition = new Vector(robot.getX(), robot.getY());
            if (Vector.distance(position, robotPosition) < tolerance) {
                return robot;
            }
        }
        return null;
    }

    private void tickRobot(Robot robot) {
        // execute next program statement
        robot.getProgram().next(robot);

        // handle movement
        robot.accelerate();
        robot.setX(robot.getX() + robot.getActualSpeedX());
        robot.setY(robot.getY() + robot.getActualSpeedY());

        // handle shot
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
