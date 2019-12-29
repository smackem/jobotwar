package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.util.Arguments;

import java.util.Objects;

/**
 * A runtime representation of a programmed robot.
 */
public class Robot {
    private final double acceleration;
    private final RobotProgram program;
    private final int rgb;
    private final int coolDownTicks;
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

    /**
     * Initializes a new instance of {@link Robot}.
     * @param acceleration The acceleration in pixels per tick^2. Must be positive.
     * @param rgb The RGB color of the robot (format in hex: 0xrrggbb).
     * @param program The program that controls the robot.
     */
    public Robot(double acceleration, int rgb, int coolDownTicks, RobotProgram program) {
        this.acceleration = Arguments.requireRange(acceleration, 0, Constants.MAX_ROBOT_ACCELERATION);
        this.rgb = rgb;
        this.coolDownTicks = coolDownTicks;
        this.program = Objects.requireNonNull(program);
        this.health = Constants.MAX_HEALTH;
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
        this.coolDownHoldOff = Arguments.requireRange(value, 0, 100_000);
    }
}
