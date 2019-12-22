package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.util.Arguments;

import java.util.Objects;

/**
 * A runtime representation of a programmed robot.
 */
public class Robot {
    private final double acceleration;
    private final RobotProgram program;
    private double speedX;
    private double speedY;
    private double actualSpeedX;
    private double actualSpeedY;
    private double aimAngle;
    private double radarAngle;
    private double x;
    private double y;
    private int shot;
    private int health;

    /**
     * Initializes a new instance of {@link Robot}.
     * @param acceleration The acceleration in pixels per tick^2. Must be positive.
     */
    public Robot(double acceleration, RobotProgram program) {
        this.acceleration = Arguments.requireRange(acceleration, 0, Constants.MAX_ROBOT_ACCELERATION);
        this.program = Objects.requireNonNull(program);
    }

    /**
     * @return The built-in acceleration of the robot in pixels per tick^2.
     */
    public double getAcceleration() {
        return this.acceleration;
    }

    /**
     * @return The program that controls the robot.
     */
    public RobotProgram getProgram() {
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
            this.actualSpeedX = Math.max(this.actualSpeedX + this.acceleration, this.speedX);
        } else if (this.actualSpeedX > this.speedX) {
            this.actualSpeedX = Math.min(this.actualSpeedX - this.acceleration, this.speedX);
        }
        if (this.actualSpeedY < this.speedY) {
            this.actualSpeedY = Math.max(this.actualSpeedY + this.acceleration, this.speedY);
        } else if (this.actualSpeedY > this.speedY) {
            this.actualSpeedY = Math.min(this.actualSpeedY - this.acceleration, this.speedY);
        }
    }

    public double getAimAngle() {
        return this.aimAngle;
    }

    public void setAimAngle(double value) {
        this.aimAngle = Arguments.requireRange(value, 0, 360 - Constants.ANGLE_PRECISION);
    }

    public double getRadarAngle() {
        return this.radarAngle;
    }

    public void setRadarAngle(double value) {
        this.radarAngle = Arguments.requireRange(value, 0, 360 - Constants.ANGLE_PRECISION);
    }

    public double getX() {
        return this.x;
    }

    public void setX(double value) {
        this.x = Arguments.requireRange(value, 0, Constants.MAX_BOARD_WIDTH);
    }

    public double getY() {
        return this.y;
    }

    public void setY(double value) {
        this.y = Arguments.requireRange(value, 0, Constants.MAX_BOARD_HEIGHT);
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int value) {
        this.health = Arguments.requireRange(value, 0, Constants.MAX_HEALTH);
    }

    public boolean isDead() {
        return this.health == 0;
    }

    public int getShot() {
        return this.shot;
    }

    public void setShot(int value) {
        this.shot = Arguments.requireRange(value, 0, Constants.MAX_SHOT);
    }
}
