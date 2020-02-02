package net.smackem.jobotwar.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Runs the game.
 */
public final class GameEngine {
    private final Board board;

    /**
     * Initializes a new instance of {@link GameEngine}.
     * @param board The game board.
     */
    public GameEngine(Board board) {
        this.board = Objects.requireNonNull(board);
    }

    /**
     * Executes the next frame.
     * @return A {@link TickResult} that holds the new state of the game.
     */
    public TickResult tick() {
        // handle robots
        //
        final TickResult result = new TickResult();
        final Collection<Robot> robots = this.board.robots();
        for (final Robot robot : robots) {
            if (robot.isDead() == false) {
                tickRobot(robot, result);
            } else {
                result.killedRobots.add(robot);
            }
        }
        robots.removeAll(result.killedRobots);

        // win condition
        //
        if (result.killedRobots.size() > 0 && robots.size() == 1) {
            result.winner = robots.iterator().next();
        } else if (result.killedRobots.size() > 1 && robots.size() == 0) {
            result.draw = true;
        }

        // handle projectiles
        //
        final Collection<Projectile> projectiles = this.board.projectiles();
        for (final Projectile projectile : projectiles) {
            if (tickProjectile(projectile)) {
                result.explodedProjectiles.add(projectile);
            }
        }
        projectiles.removeAll(result.explodedProjectiles);
        return result;
    }

    /**
     * The current state of the game as changed by a call to {@link #tick()}.
     */
    public static final class TickResult {
        private final Collection<Projectile> explodedProjectiles;
        private final Collection<Vector> collisionPositions;
        private final Collection<Robot> killedRobots;
        private final Collection<RadarBeam> radarBeams;
        private Robot winner;
        private boolean draw;

        /**
         * @return an unmodifiable collection of projectiles that have exploded.
         */
        public Collection<Projectile> explodedProjectiles() {
            return this.explodedProjectiles;
        }

        /**
         * @return an unmodifiable collection of the positions where robots collide - either
         *      with other robots or with walls.
         */
        public Collection<Vector> collisionPositions() {
            return this.collisionPositions;
        }

        /**
         * @return an unmodifiable collection of the robots that have been killed.
         */
        public Collection<Robot> killedRobots() {
            return this.killedRobots;
        }

        /**
         * @return an unmodifiable collection of radar beams that have been emitted by robots.
         */
        public Collection<RadarBeam> radarBeams() {
            return this.radarBeams;
        }

        /**
         * @return the robot that has won the game. This is only returned once, if robots have
         *      been killed and only one robot remains.
         */
        public Robot winner() {
            return this.winner;
        }

        /**
         * @return {@code true} if the game ended with a draw. {@link #winner()} is {@code null}
         *      in this case.
         */
        public boolean isDraw() {
            return this.draw;
        }

        public boolean hasEnded() {
            return this.draw || this.winner != null;
        }

        private TickResult() {
            this.explodedProjectiles = new ArrayList<>();
            this.collisionPositions = new ArrayList<>();
            this.killedRobots = new ArrayList<>();
            this.radarBeams = new ArrayList<>();
        }
    }

    private boolean tickProjectile(Projectile projectile) {
        final Collection<Robot> hitRobots = hitTestRobots(projectile.position(), projectile.sourceRobot(), 0);
        if (hitRobots.isEmpty() == false || isProjectileExploding(projectile)) {
            explodeProjectile(projectile);
            return true;
        }
        projectile.incrementPosition();
        return false;
    }

    private boolean isProjectileExploding(Projectile projectile) {
        final Vector position = projectile.position();
        final double x = position.x();
        final double y = position.y();

        // out of bounds?
        if (0 >= x || x >= this.board.width()) {
            return true;
        }
        if (0 >= y || y >= this.board.height()) {
            return true;
        }

        // at destination?
        return position.equals(projectile.destination());
    }

    private void explodeProjectile(Projectile projectile) {
        final Collection<Robot> damagedRobots = hitTestRobots(projectile.position(),
                null,Constants.EXPLOSION_RADIUS);
        for (final Robot robot : damagedRobots) {
            robot.setHealth(Math.max(0, robot.getHealth() - 30));
        }
    }

    // returns a list of robots whose distance from @position is less than @tolerance
    private Collection<Robot> hitTestRobots(Vector position, Robot excludeRobot, double tolerance) {
        final Collection<Robot> hitRobots = new ArrayList<>();
        for (final Robot robot : this.board.robots()) {
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
        robot.program().next();

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
            robot.setLatestRadarBeam(beam);
            robot.setRadarAngle(null);
        }

        // handle cool down and shot
        handleGun(robot);
    }

    private RadarBeam calcRadarBeam(Robot robot, double radarAngle) {
        final double radarAngleRadians = Math.toRadians(radarAngle);
        final Vector position = robot.getPosition();
        final Line line = new Line(position,
                Vector.fromAngleAndLength(radarAngleRadians, Constants.MAX_RADAR_RANGE));

        // detect nearest robot
        Vector nearestRobotPos = null;
        double nearestRobotDistance = 0;
        for (final Robot r : this.board.robots()) {
            if (r == robot) {
                continue;
            }
            final Vector p = r.getPosition();
            final double distanceFromBeam = line.distanceFromPoint(p);
            if (distanceFromBeam >= Constants.ROBOT_RADIUS) {
                continue;
            }
            final double robotDistance = Vector.distance(position, p);
            final Vector beamPos = position.add(Vector.fromAngleAndLength(radarAngleRadians, robotDistance));
            if (beamPos.isCloseTo(p, Constants.ROBOT_RADIUS) == false) {
                continue;
            }
            if (robotDistance < nearestRobotDistance || nearestRobotPos == null) {
                nearestRobotDistance = robotDistance;
                nearestRobotPos = beamPos;
            }
        }
        if (nearestRobotPos != null) {
            return new RadarBeam(robot, nearestRobotPos, RadarBeamHitKind.ROBOT);
        }

        // detect wall
        radarAngle = radarAngle < 0 ? radarAngle + 360 : radarAngle;
        final Line wall;
        if (45 <= radarAngle && radarAngle <= 135) { // bottom wall
            wall = new Line(new Vector(0, this.board.height()), new Vector(this.board.width(), this.board.height()));
        } else if (136 <= radarAngle && radarAngle <= 225) { // left wall
            wall = new Line(Vector.ORIGIN, new Vector(0, this.board.height()));
        } else if (226 <= radarAngle && radarAngle <= 315) { // top wall
            wall = new Line(Vector.ORIGIN, new Vector(this.board.width(), 0));
        } else { // right wall
            wall = new Line(new Vector(this.board.width(), 0), new Vector(this.board.width(), this.board.height()));
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
        } else if (nextX >= this.board.width() - Constants.ROBOT_RADIUS) {
            hasCollision = true;
            nextX = this.board.width() - Constants.ROBOT_RADIUS;
            collisionX = this.board.width();
            collisionY = nextY;
        }
        if (nextY <= Constants.ROBOT_RADIUS) {
            hasCollision = true;
            nextY = Constants.ROBOT_RADIUS;
            collisionX = nextX;
            collisionY = 0;
        } else if (nextY >= this.board.height() - Constants.ROBOT_RADIUS) {
            hasCollision = true;
            nextY = this.board.height() - Constants.ROBOT_RADIUS;
            collisionX = nextX;
            collisionY = this.board.height();
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
            final Vector dest = Vector.fromAngleAndLength(angle, shot);
            final Projectile projectile = new Projectile(robot, position.add(dest), Constants.PROJECTILE_SPEED);
            this.board.projectiles().add(projectile);
            robot.setShot(0);
            robot.setCoolDownHoldOff(robot.coolDownTicks());
        }
    }
}
