package net.smackem.jobotwar.runtime;

/**
 * A projectile that has been fired by a robot.
 */
public class Projectile {
    private final Robot sourceRobot;
    private final Vector destination;
    private final Vector increment;
    private final double speed;
    private Vector position;
    private double remainingDistance;

    /**
     * Initializes a new instance of {@link Projectile}.
     *
     * @param sourceRobot The robot that has fired the projectile.
     * @param destination The calculated destination of the projectile, where it should explode.
     * @param speed The speed of the projectile in pixels per tick.
     */
    public Projectile(Robot sourceRobot, Vector destination, double speed) {
        this.sourceRobot = sourceRobot;
        this.destination = destination;
        this.speed = speed;
        this.position = new Vector(sourceRobot.getX(), sourceRobot.getY());
        final Vector difference = destination.subtract(this.position);
        this.increment = difference.normalize().multiply(speed);
        this.remainingDistance = difference.length();
    }

    /**
     * @return The robot that has fired the projectile.
     */
    public Robot sourceRobot() {
        return this.sourceRobot;
    }

    /**
     * @return The calculated destination where the projectile is about to explode.
     */
    public Vector destination() {
        return this.destination;
    }

    /**
     * @return The current position of the projectile.
     */
    public Vector position() {
        return this.position;
    }

    /**
     * @return The speed of the projectile in pixels per tick.
     */
    public double speed() {
        return this.speed;
    }

    /**
     * Advances the projectile's position according to its current position, destination and speed.
     * If the projectile is at its destination ({@link #position()} {@code ==} {@link #destination()}),
     * the method does nothing. The method makes sure that the projectile will exactly hit its destination
     * in the end.
     */
    public void incrementPosition() {
        if (this.position.equals(this.destination)) {
            return;
        }

        double tolerance = this.speed / 2;
        if (remainingDistance <= tolerance) {
            this.position = this.destination;
            return;
        }

        this.position = this.position.add(this.increment);
        this.remainingDistance -= this.speed;
    }
}
