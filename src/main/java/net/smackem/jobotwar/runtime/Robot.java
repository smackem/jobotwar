package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.util.Arguments;

import java.util.Objects;
import java.util.function.Function;

/**
 * A runtime representation of a programmed robot.
 */
public class Robot {
    private final String name;
    private final double acceleration;
    private final RobotProgram program;
    private final int rgb;
    private final int coolDownTicks;
    private final String imageUrl;
    private double speedX;
    private double speedY;
    private double actualSpeedX;
    private double actualSpeedY;
    private double aimAngle;
    private Double radarAngle;
    private double x;
    private double y;
    private int shot;
    private int health;
    private int coolDownHoldOff;
    private Vector cachedPosition;
    private RadarBeam latestRadarBeam;

    /**
     * Initializes a new instance of {@link Robot}.
     */
    private Robot(Builder builder) {
        this.name = builder.name;
        this.acceleration = builder.acceleration;
        this.rgb = builder.rgb;
        this.coolDownTicks = builder.coolDownTicks;
        this.health = Constants.MAX_HEALTH;
        this.program = builder.programFactory.apply(this);
        this.x = builder.x;
        this.y = builder.y;
        this.imageUrl = builder.imageUrl;
    }

    /**
     * Builds a {@link Robot}.
     */
    public static class Builder {
        private final Function<Robot, RobotProgram> programFactory;
        private String name = "Rob";
        private int rgb = 0xffffff;
        private double acceleration = Constants.DEFAULT_ROBOT_ACCELERATION;
        private int coolDownTicks = Constants.DEFAULT_COOL_DOWN_TICKS;
        private double x;
        private double y;
        private String imageUrl;

        /**
         * Initializes a new instance of {@link Builder}.
         * @param programFactory Called by the constructor to create the program that controls the robot.
         */
        public Builder(Function<Robot, RobotProgram> programFactory) {
            this.programFactory = programFactory;
        }

        /**
         * The name of the robot.
         */
        public Builder name(String value) {
            this.name = value;
            return this;
        }

        /**
         * The RGB color of the robot (format in hex: 0xrrggbb).
         */
        public Builder rgb(int value) {
            this.rgb = value;
            return this;
        }

        /**
         * The acceleration in pixels per tick^2. Must be positive.
         */
        public Builder acceleration(int value) {
            this.acceleration = Arguments.requireRange(value, 0, Constants.MAX_ROBOT_ACCELERATION);
            return this;
        }

        /**
         * The number of ticks the robot's gun needs to cool down (so it can fire again).
         */
        public Builder coolDownTicks(int value) {
            this.coolDownTicks = value;
            return this;
        }

        /**
         * The initial x-position.
         */
        public Builder x(double value) {
            this.x = value;
            return this;
        }

        /**
         * The initial y-position.
         */
        public Builder y(double value) {
            this.y = value;
            return this;
        }

        /**
         * The url of the robot icon image.
         */
        public Builder imageUrl(String value) {
            this.imageUrl = value;
            return this;
        }

        /**
         * @return A new {@link Robot}.
         */
        public Robot build() {
            return new Robot(this);
        }
    }

    /**
     * @return The name of the robot.
     */
    public String name() {
        return this.name;
    }

    /**
     * @return The built-in acceleration of the robot in pixels per tick^2.
     */
    public double acceleration() {
        return this.acceleration;
    }

    /**
     * @return The RGB color of the robot (format in hex: 0xrrggbb)
     */
    public int rgb() {
        return this.rgb;
    }

    /**
     * @return The number of ticks the gun needs to cool down between shots.
     */
    public int coolDownTicks() {
        return this.coolDownTicks;
    }

    /**
     * @return The program that controls the robot.
     */
    public RobotProgram program() {
        return this.program;
    }

    /**
     * @return The URL of the robot's icon image or {@code null} if none.
     */
    public String imageUrl() {
        return this.imageUrl;
    }

    /**
     * @return The target speed in x-direction, in pixels per tick.
     */
    public double getSpeedX() {
        return this.speedX;
    }

    /**
     * Sets the target speed in x-direction, in pixels per tick.
     */
    public void setSpeedX(double value) {
        this.speedX = Arguments.requireRange(value, -Constants.MAX_ROBOT_SPEED, Constants.MAX_ROBOT_SPEED);
    }

    /**
     * @return The target speed in y-direction, in pixels per tick.
     */
    public double getSpeedY() {
        return this.speedY;
    }

    /**
     * Sets the target speed in y-direction, in pixels per tick.
     */
    public void setSpeedY(double value) {
        this.speedY = Arguments.requireRange(value, -Constants.MAX_ROBOT_SPEED, Constants.MAX_ROBOT_SPEED);
    }

    /**
     * @return The actual speed in x-direction, in pixels per tick.
     */
    public double getActualSpeedX() {
        return this.actualSpeedX;
    }

    /**
     * @return The actual speed in y-direction, in pixels per tick.
     */
    public double getActualSpeedY() {
        return this.actualSpeedY;
    }

    /**
     * Accelerates the robot, if the actual speed does not match the target speed. The actual speed
     * is adjusted by the built-in acceleration. Does nothing if actual speed matches target speed.
     */
    public void accelerate() {
        if (this.actualSpeedX < this.speedX) {
            this.actualSpeedX = Math.min(this.actualSpeedX + this.acceleration, this.speedX);
        } else if (this.actualSpeedX > this.speedX) {
            this.actualSpeedX = Math.max(this.actualSpeedX - this.acceleration, this.speedX);
        }
        if (this.actualSpeedY < this.speedY) {
            this.actualSpeedY = Math.min(this.actualSpeedY + this.acceleration, this.speedY);
        } else if (this.actualSpeedY > this.speedY) {
            this.actualSpeedY = Math.max(this.actualSpeedY - this.acceleration, this.speedY);
        }
    }

    /**
     * @return The angle at which the gun aims, in degrees.
     */
    public double getAimAngle() {
        return this.aimAngle;
    }

    /**
     * Sets the angle at which the gun aims, in degrees.
     *       270
     *   225    315
     * 180          0
     *   135     45
     *       90
     */
    public void setAimAngle(double value) {
        this.aimAngle = Arguments.requireRange(value,
                -180, 360 - Constants.ANGLE_PRECISION);
    }

    /**
     * @return The angle at which the radar aims, in degrees - or {@code null} if radar is not active.
     */
    public Double getRadarAngle() {
        return this.radarAngle;
    }

    /**
     * Sets the angle at which the radar aims, in degrees - or {@code null} if radar should not be active.
     *       270
     *   225    315
     * 180          0
     *   135     45
     *       90
     */
    public void setRadarAngle(Double value) {
        this.radarAngle = value != null
            ? Arguments.requireRange(value,-180, 360 - Constants.ANGLE_PRECISION)
            : null;
    }

    /**
     * @return The last emitted radar beam.
     */
    public RadarBeam getLatestRadarBeam() {
        return this.latestRadarBeam;
    }

    /**
     * Sets the last emitted radar beam.
     */
    public void setLatestRadarBeam(RadarBeam value) {
        this.latestRadarBeam = value;
    }

    /**
     * @return The current x-position of the robot.
     */
    public double getX() {
        return this.x;
    }

    /**
     * Sets the current x-position of the robot.
     */
    public void setX(double value) {
        this.x = Arguments.requireRange(value, 0, Constants.MAX_BOARD_WIDTH);
        this.cachedPosition = null;
    }

    /**
     * @return The current y-position of the robot.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Sets the current y-position of the robot.
     */
    public void setY(double value) {
        this.y = Arguments.requireRange(value, 0, Constants.MAX_BOARD_HEIGHT);
        this.cachedPosition = null;
    }

    /**
     * @return The position of the robot as a {@link Vector}.
     */
    public Vector getPosition() {
        if (this.cachedPosition == null) {
            this.cachedPosition = new Vector(this.x, this.y);
        }
        return this.cachedPosition;
    }

    /**
     * @return The current health of the robot in the range 0..100, where 0 means dead.
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * Sets the current health of the robot in the range 0..100, where 0 means dead.
     */
    public void setHealth(int value) {
        this.health = Arguments.requireRange(value, 0, Constants.MAX_HEALTH);
    }

    /**
     * @return {@code true} if the robot has health 0, otherwise {@code false}.
     */
    public boolean isDead() {
        return this.health == 0;
    }

    /**
     * @return The value of the SHOT register as set by the program, meaning the distance to shoot.
     */
    public int getShot() {
        return this.shot;
    }

    /**
     * Sets the value of SHOT register, meaning the distance to shoot.
     */
    public void setShot(int value) {
        this.shot = Arguments.requireRange(value, 0, Constants.MAX_SHOT);
    }

    /**
     * @return The number of ticks that the robot needs to wait until it can fire the gun again.
     */
    public int getCoolDownHoldOff() {
        return this.coolDownHoldOff;
    }

    /**
     * Sets the number of ticks that the robot needs to wait until it can fire the gun again.
     */
    public void setCoolDownHoldOff(int value) {
        this.coolDownHoldOff = Arguments.requireRange(value, 0, this.coolDownTicks);
    }
}
