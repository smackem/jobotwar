package net.smackem.jobotwar.robots;

import net.smackem.jobotwar.util.Arguments;

public class Robot {
    private int speedX;
    private int speedY;
    private int aimAngle;
    private int radarAngle;
    private int x;
    private int y;
    private int health;
    private int shot;

    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }

    public int getAimAngle() {
        return aimAngle;
    }

    public void setAimAngle(int aimAngle) {
        this.aimAngle = Arguments.requireRange(aimAngle, 0, 359);
    }

    public int getRadarAngle() {
        return radarAngle;
    }

    public void setRadarAngle(int radarAngle) {
        this.radarAngle = Arguments.requireRange(radarAngle, 0, 359);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = Arguments.requireRange(x, 0, 256);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = Arguments.requireRange(x, 0, 256);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Arguments.requireRange(x, 0, 100);
    }

    public boolean isDead() {
        return this.health == 0;
    }

    public int getShot() {
        return shot;
    }

    public void setShot(int shot) {
        this.shot = shot;
    }
}
