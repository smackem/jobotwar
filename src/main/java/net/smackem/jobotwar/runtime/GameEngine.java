package net.smackem.jobotwar.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public final class GameEngine {
    private final Board board;

    public GameEngine(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    public TickResult tick() {
        final TickResult result = new TickResult();
        for (final Robot robot : this.board.getRobots()) {
            if (robot.isDead() == false) {
                tickRobot(robot, result.collisionPositions);
                if (robot.isDead()) {
                    result.killedRobots.add(robot);
                }
            }
        }

        for (final Projectile projectile : this.board.projectiles()) {
            if (tickProjectile(projectile)) {
                result.explodedProjectiles.add(projectile);
            }
        }
        this.board.projectiles().removeAll(result.explodedProjectiles);
        return result;
    }

    public static final class TickResult {
        public final Collection<Projectile> explodedProjectiles;
        public final Collection<Vector> collisionPositions;
        public final Collection<Robot> killedRobots;

        private TickResult() {
            this.explodedProjectiles = new ArrayList<>();
            this.collisionPositions = new ArrayList<>();
            this.killedRobots = new ArrayList<>();
        }
    }

    private boolean tickProjectile(Projectile projectile) {
        final Collection<Robot> hitRobots = hitTestRobots(projectile.getPosition(), projectile.getSourceRobot(), 0);
        if (hitRobots.isEmpty() == false || isProjectileExploding(projectile)) {
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
        final Collection<Robot> damagedRobots = hitTestRobots(projectile.getPosition(),
                null,Constants.EXPLOSION_RADIUS);
        for (final Robot robot : damagedRobots) {
            robot.setHealth(Math.max(0, robot.getHealth() - 30));
        }
    }

    private Collection<Robot> hitTestRobots(Vector position, Robot excludeRobot, double tolerance) {
        final Collection<Robot> hitRobots = new ArrayList<>();
        for (final Robot robot : this.board.getRobots()) {
            if (robot == excludeRobot) {
                continue;
            }
            final Vector robotPosition = new Vector(robot.getX(), robot.getY());
            if (Vector.distance(position, robotPosition) < Constants.ROBOT_RADIUS + tolerance) {
                hitRobots.add(robot);
            }
        }
        return hitRobots;
    }

    private void tickRobot(Robot robot, Collection<Vector> collisions) {
        // execute next program statement
        robot.getProgram().next(robot);

        // handle movement
        robot.accelerate();
        if (moveRobot(robot, collisions)) {
            robot.setHealth(Math.max(0, robot.getHealth() - 10));
        }

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

    private boolean moveRobot(Robot robot, Collection<Vector> collisions) {
        double nextX = robot.getX() + robot.getActualSpeedX();
        double nextY = robot.getY() + robot.getActualSpeedY();

        // collisions with wall?
        boolean hasCollision = false;
        double collisionX = 0, collisionY = 0;
        if (nextX <= Constants.ROBOT_RADIUS) {
            hasCollision = true;
            nextX = Constants.ROBOT_RADIUS;
            collisionX = 0;
            collisionY = nextY;
        } else if (nextX >= this.board.getWidth() - Constants.ROBOT_RADIUS) {
            hasCollision = true;
            nextX = this.board.getWidth() - Constants.ROBOT_RADIUS;
            collisionX = this.board.getWidth();
            collisionY = nextY;
        }
        if (nextY <= Constants.ROBOT_RADIUS) {
            hasCollision = true;
            nextY = Constants.ROBOT_RADIUS;
            collisionX = nextX;
            collisionY = 0;
        } else if (nextY >= this.board.getHeight() - Constants.ROBOT_RADIUS) {
            hasCollision = true;
            nextY = this.board.getHeight() - Constants.ROBOT_RADIUS;
            collisionX = nextX;
            collisionY = this.board.getHeight();
        }

        if (hasCollision) {
            collisions.add(new Vector(collisionX, collisionY));
        }

        // collisions with other robots?
        final Vector position = new Vector(nextX, nextY);
        final Collection<Robot> collidingRobots = hitTestRobots(position, robot, Constants.ROBOT_RADIUS);
        if (collidingRobots.isEmpty() == false) {
            hasCollision = true;
            for (final Robot collidingRobot : collidingRobots) {
                final Vector p = new Vector(collidingRobot.getX(), collidingRobot.getY());
                final Vector center = position.add(p.subtract(position).divide(2));
                collisions.add(center);
            }
        }

        robot.setX(nextX);
        robot.setY(nextY);
        return hasCollision;
    }
}
