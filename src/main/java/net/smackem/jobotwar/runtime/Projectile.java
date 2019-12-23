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

    public Vector getPosition() {
        return this.position;
    }

    public double getSpeed() {
        return this.speed;
    }

    public void incrementPosition() {
        if (this.position.equals(this.destination)) {
            return;
        }

        double tolerance = this.speed / 2;
        if (Vector.distance(this.position, this.destination) < tolerance) {
            this.position = this.destination;
            return;
        }

        this.position = this.position.add(this.directionUnit.multiply(this.speed));
    }
}
