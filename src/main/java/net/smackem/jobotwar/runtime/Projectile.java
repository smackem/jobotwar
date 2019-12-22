package net.smackem.jobotwar.runtime;

public class Projectile {
    private final Robot sourceRobot;
    private final Vector destination;
    private final Vector directionUnit;
    private final double speed;
    private Vector position;

    public Projectile(Robot sourceRobot, Vector destination, double speed) {
        this.sourceRobot = sourceRobot;
        this.destination = destination;
        this.speed = speed;
        this.position = new Vector(sourceRobot.getX(), sourceRobot.getY());
        this.directionUnit = destination.subtract(this.position).normalize();
    }

    public Robot getSourceRobot() {
        return this.sourceRobot;
    }

    public Vector getDestination() {
        return this.destination;
    }

    public double getX() {
        return this.position.getX();
    }

    public double getY() {
        return this.position.getY();
    }

    public double getSpeed() {
        return this.speed;
    }

    public Vector incrementPosition() {
        this.position = this.position.add(this.directionUnit.multiply(this.speed));
        return this.position;
    }
}
