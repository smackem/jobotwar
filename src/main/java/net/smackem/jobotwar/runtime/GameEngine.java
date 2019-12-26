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
                tickRobot(robot, result);
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
        public final Collection<RadarBeam> radarBeams;

        private TickResult() {
            this.explodedProjectiles = new ArrayList<>();
            this.collisionPositions = new ArrayList<>();
            this.killedRobots = new ArrayList<>();
            this.radarBeams = new ArrayList<>();
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

    // returns a list of robots whose distance from @position is less than @tolerance
    private Collection<Robot> hitTestRobots(Vector position, Robot excludeRobot, double tolerance) {
        final Collection<Robot> hitRobots = new ArrayList<>();
        for (final Robot robot : this.board.getRobots()) {
            if (robot == excludeRobot) {
                continue;
            }
            final Vector robotPosition = robot.getPosition();
            if (Vector.distance(position, robotPosition) < Constants.ROBOT_RADIUS + tolerance) {
                hitRobots.add(robot);
            }
        }
        return hitRobots;
    }

    private void tickRobot(Robot robot, TickResult result) {
        // execute next program statement
        robot.getProgram().next(robot);

        // handle movement
        robot.accelerate();
        if (moveRobot(robot, result.collisionPositions)) {
            robot.setHealth(Math.max(0, robot.getHealth() - 5));
        }

        // handle radar
        final Double radarAngle = robot.getRadarAngle();
        if (radarAngle != null) {
            final RadarBeam beam = calcRadarBeam(robot, radarAngle);
            result.radarBeams.add(beam);
            robot.setRadarAngle(null);
        }

        // handle cool down and shot
        handleGun(robot);
    }

    private RadarBeam calcRadarBeam(Robot robot, double radarAngle) {
        final Vector position = robot.getPosition();
        final Line line = Line.fromAngleAndLength(position, Math.toRadians(radarAngle), Constants.MAX_RADAR_RANGE);

        // detect nearest robot
        Vector nearestRobotPos = null;
        double nearestRobotDistance = 0;
        for (final Robot r : this.board.getRobots()) {
            if (r == robot) {
                continue;
            }
            final Vector p = r.getPosition();
            final double distanceFromBeam = line.distanceFromPoint(p);
            if (distanceFromBeam >= Constants.ROBOT_RADIUS) {
                continue;
            }
            final double robotDistance = Vector.distance(position, p);
            if (robotDistance < nearestRobotDistance || nearestRobotPos == null) {
                nearestRobotDistance = robotDistance;
                nearestRobotPos = p;
            }
        }
        if (nearestRobotPos != null) {
            return new RadarBeam(robot, nearestRobotPos, RadarBeamHitKind.ROBOT);
        }

        // detect wall
        radarAngle = radarAngle < 0 ? radarAngle + 360 : radarAngle;
        final Line wall;
        if (45 <= radarAngle && radarAngle <= 135) { // top wall
            wall = new Line(Vector.ORIGIN, new Vector(this.board.getWidth(), 0));
        } else if (136 <= radarAngle && radarAngle <= 225) { // left wall
            wall = new Line(Vector.ORIGIN, new Vector(0, this.board.getHeight()));
        } else if (226 <= radarAngle && radarAngle <= 315) { // bottom wall
            wall = new Line(new Vector(0, this.board.getHeight()), new Vector(this.board.getWidth(), this.board.getHeight()));
        } else { // right wall
            wall = new Line(new Vector(this.board.getWidth(), 0), new Vector(this.board.getWidth(), this.board.getHeight()));
        }
        final Vector wallIntersection = Line.intersect(line, wall);
        assert(wallIntersection != null);
        return new RadarBeam(robot, wallIntersection, RadarBeamHitKind.WALL);
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
                final Vector p = collidingRobot.getPosition();
                final Vector center = position.add(p.subtract(position).divide(2));
                collisions.add(center);
            }
        }

        robot.setX(nextX);
        robot.setY(nextY);
        return hasCollision;
    }

    private void handleGun(Robot robot) {
        final int coolDownHoldOff = robot.getCoolDownHoldOff();
        if (coolDownHoldOff > 0) {
            robot.setCoolDownHoldOff(coolDownHoldOff - 1);
            return;
        }

        final int shot = robot.getShot();
        if (shot > 0) {
            final double angle = Math.toRadians(robot.getAimAngle());
            final Vector position = robot.getPosition();
            final Vector dest = new Vector(Math.cos(angle) * shot, Math.sin(angle) * shot);
            final Projectile projectile = new Projectile(robot, position.add(dest), Constants.PROJECTILE_SPEED);
            this.board.projectiles().add(projectile);
            robot.setShot(0);
            robot.setCoolDownHoldOff(robot.getCoolDownTicks());
        }
    }
}
