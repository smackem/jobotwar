package net.smackem.jobotwar.runtime;

import java.util.Comparator;
import java.util.Objects;

/**
 * An immutable 2D vector.
 */
public final class Vector {
    private final double x;
    private final double y;

    public static final Vector ORIGIN = new Vector(0, 0);

    /**
     * Initializes a new instance of {@link Vector}.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return The x-coordinate.
     */
    public double getX() {
        return this.x;
    }

    /**
     * @return The y-coordinate.
     */
    public double getY() {
        return this.y;
    }

    /**
     * @return The length or magnitude of the vector (distance from origin).
     */
    public double getLength() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * Creates a normalized vector with the same direction as this vector.
     * @return A vector with the length 1.
     */
    public Vector normalize() {
        final double im = 1.0 / getLength();
        return new Vector(x * im, y * im);
    }

    /**
     * Adds a vector to this vector.
     * @param right the vector to add.
     * @return A new vector that is the sum of the two vectors.
     */
    public Vector add(Vector right) {
        Objects.requireNonNull(right);
        return new Vector(this.x + right.x, this.y + right.y);
    }

    /**
     * Subtracts a vector from this vector.
     * @param right the vector to subtract.
     * @return A new vector that is the difference between the two vectors.
     */
    public Vector subtract(Vector right) {
        Objects.requireNonNull(right);
        return new Vector(this.x - right.x, this.y - right.y);
    }

    /**
     * Multiplies this vector with a scalar.
     * @param scalar the scalar to multiply with.
     * @return A new vector that is the product of this vector and the scalar.
     */
    public Vector multiply(double scalar) {
        return new Vector(this.x * scalar, this.y * scalar);
    }

    /**
     * Divides this vector by a scalar.
     * @param scalar the scalar to divide by.
     * @return A new vector that is the quotient of the division.
     */
    public Vector divide(double scalar) {
        return new Vector(this.x / scalar, this.y / scalar);
    }

    /**
     * Negates this vector.
     * @return A new vector with negated coordinates.
     */
    public Vector negate() {
        return new Vector(-this.x, -this.y);
    }

    /**
     * Calculates the distance between two vectors, which is equal to the length of the
     * difference vector of the two vectors.
     * @param a first vector
     * @param b second vector.
     * @return The distance between the two vectors.
     */
    public static double distance(Vector a, Vector b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        final double dx = b.x - a.x;
        final double dy = b.y - a.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Vector vector = (Vector) o;
        return Double.compare(vector.x, this.x) == 0 && Double.compare(vector.y, this.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
        return String.format("Vector{x=%f, y=%f}", this.x, this.y);
    }
}
