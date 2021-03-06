package net.smackem.jobotwar.runtime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Runs the game.
 */
public final class GameEngine {
    private static final Logger log = LoggerFactory.getLogger(GameEngine.class);
    private final Board board;
    private final Geometry wallBoxGeometry;

    /**
     * Initializes a new instance of {@link GameEngine}.
     * @param board The game board.
     */
    public GameEngine(Board board) {
        this.board = Objects.requireNonNull(board);
        final double width = board.width(), height = board.height();
        this.wallBoxGeometry = EngineObject.GEOMETRY_FACTORY.createLineString(
                new Coordinate[] {
                        new Coordinate(0, 0),
                        new Coordinate(width, 0),
                        new Coordinate(width, height),
                        new Coordinate(0, height),
                        new Coordinate(0, 0),
                });
    }

    /**
     * @return The game board.
     */
    public Board board() {
        return this.board;
    }

    /**
     * Executes the next frame.
     * @return A {@link TickResult} that holds the new state of the game.
     * @throws RobotProgramException A robot has encountered an error upon executing its control program.
     */
    public TickResult tick() throws RobotProgramException {
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

        /**
         * @return {@code true} if the game has ended with either a {@link #winner()} or a {@link #isDraw()}.
         */
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
        final Vector projectilePos = projectile.position();
        final Collection<Robot> damagedRobots = hitTestRobots(projectilePos,null,
                Constants.EXPLOSION_RADIUS);
        for (final Robot robot : damagedRobots) {
            final double distance = Vector.distance(robot.position(), projectilePos);
            final int damage = (int)(30.0 - 20.0 * distance / (Constants.EXPLOSION_RADIUS + Constants.ROBOT_RADIUS));
            log.debug("damage: {}", damage);
            robot.setHealth(Math.max(0, robot.getHealth() - damage));
        }
    }

    // returns a list of robots whose distance from @position is less than @tolerance
    private Collection<Robot> hitTestRobots(Vector position, Robot excludeRobot, double tolerance) {
        final Collection<Robot> hitRobots = new ArrayList<>();
        for (final Robot robot : this.board.robots()) {
            if (robot == excludeRobot) {
                continue;
            }
            final Vector robotPosition = robot.position();
            if (Vector.distance(position, robotPosition) < Constants.ROBOT_RADIUS + tolerance) {
                hitRobots.add(robot);
            }
        }
        return hitRobots;
    }

    private void tickRobot(Robot robot, TickResult result) throws RobotProgramException {
        try {
            tickRobotInternal(robot, result);
        } catch (RobotProgramException e) {
            throw e;
        } catch (Exception e) {
            throw new RobotProgramException(robot.name(), e.getMessage(), e);
        }
    }

    private void tickRobotInternal(Robot robot, TickResult result) throws RobotProgramException {
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
        final Vector position = robot.position();
        final Line line = new Line(position,
                position.add(Vector.fromAngleAndLength(radarAngleRadians, Constants.MAX_RADAR_RANGE)));

        // detect robot
        for (final Robot r : this.board.robots()) {
            if (r == robot) {
                continue;
            }
            final Geometry intersection = line.geometry().intersection(r.geometry());
            final Coordinate coordinate = intersection.getCoordinate();
            if (coordinate != null) {
                return new RadarBeam(robot, new Vector(coordinate), RadarBeamHitKind.ROBOT);
            }
        }

        // detect wall
        final Geometry wallIntersection = line.geometry().intersection(this.wallBoxGeometry);
        assert(wallIntersection instanceof Point && wallIntersection.getCoordinate() != null);
        return new RadarBeam(robot, new Vector(wallIntersection.getCoordinate()), RadarBeamHitKind.WALL);
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
                final Vector p = collidingRobot.position();
                final Vector center = position.add(p.subtract(position).divide(2));
                collisions.add(center);
            }
        }

        robot.setPosition(nextX, nextY);
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
            final Vector position = robot.position();
            final Vector dest = Vector.fromAngleAndLength(angle, shot);
            final Projectile projectile = new Projectile(robot, position.add(dest), Constants.PROJECTILE_SPEED);
            this.board.projectiles().add(projectile);
            robot.setShot(0);
            robot.setCoolDownHoldOff(robot.coolDownTicks());
        }
    }
}
