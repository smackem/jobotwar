package net.smackem.jobotwar.runtime;

import org.locationtech.jts.geom.Coordinate;

import java.util.Objects;

/**
 * An immutable 2D vector.
 */
public final class Vector {
    final Coordinate coordinate;

    public static final Vector ORIGIN = new Vector(0, 0);

    /**
     * Initializes a new instance of {@link Vector}.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     */
    public Vector(double x, double y) {
        this.coordinate = new Coordinate(x, y);
    }

    Vector(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    /**
     * Creates a new {@link Vector} with the given angle from the x-axis and the given length.
     * @param angle The angle between the vector and the x-axis, in radians.
     * @param length The length of the new vector.
     * @return A new instance of {@link Vector}.
     */
    public static Vector fromAngleAndLength(double angle, double length) {
        return new Vector(Math.cos(angle) * length, Math.sin(angle) * length);
    }

    /**
     * @return The x-coordinate.
     */
    public double x() {
        return this.coordinate.x;
    }

    /**
     * @return The y-coordinate.
     */
    public double y() {
        return this.coordinate.y;
    }

    /**
     * @return The length or magnitude of the vector (distance from origin).
     */
    public double length() {
        return Math.sqrt(this.coordinate.x * this.coordinate.x + this.coordinate.y * this.coordinate.y);
    }

    /**
     * Creates a normalized vector with the same direction as this vector.
     * @return A vector with the length 1.
     */
    public Vector normalize() {
        final double mag = length();
        final double im = mag != 0.0 ? 1.0 / mag : 0.0;
        return new Vector(this.coordinate.x * im, this.coordinate.y * im);
    }

    /**
     * Adds a vector to this vector.
     * @param right the vector to add.
     * @return A new vector that is the sum of the two vectors.
     */
    public Vector add(Vector right) {
        Objects.requireNonNull(right);
        return new Vector(this.coordinate.x + right.coordinate.x, this.coordinate.y + right.coordinate.y);
    }

    /**
     * Subtracts a vector from this vector.
     * @param right the vector to subtract.
     * @return A new vector that is the difference between the two vectors.
     */
    public Vector subtract(Vector right) {
        Objects.requireNonNull(right);
        return new Vector(this.coordinate.x - right.coordinate.x, this.coordinate.y - right.coordinate.y);
    }

    /**
     * Multiplies this vector with a scalar.
     * @param scalar the scalar to multiply with.
     * @return A new vector that is the product of this vector and the scalar.
     */
    public Vector multiply(double scalar) {
        return new Vector(this.coordinate.x * scalar, this.coordinate.y * scalar);
    }

    /**
     * Divides this vector by a scalar.
     * @param scalar the scalar to divide by.
     * @return A new vector that is the quotient of the division.
     */
    public Vector divide(double scalar) {
        return new Vector(this.coordinate.x / scalar, this.coordinate.y / scalar);
    }

    /**
     * Negates this vector.
     * @return A new vector with negated coordinates.
     */
    public Vector negate() {
        return new Vector(-this.coordinate.x, -this.coordinate.y);
    }

    /**
     * Determines whether the {@code other} {@link Vector} is close to this {@link Vector}.
     * @param other The other vector.
     * @param tolerance The positive tolerance. If the absolute x- and y-differences are within
     *                  the tolerance, the vectors are considered close.
     * @return {@code true} if the vectors are close, otherwise {@code false}.
     */
    public boolean isCloseTo(Vector other, double tolerance) {
        return Math.abs(this.coordinate.x - other.coordinate.x) < tolerance
               && Math.abs(this.coordinate.y - other.coordinate.y) < tolerance;
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
        return a.coordinate.distance(b.coordinate);
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
        return Double.compare(vector.coordinate.x, this.coordinate.x) == 0
               && Double.compare(vector.coordinate.y, this.coordinate.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.coordinate.x, this.coordinate.y);
    }

    @Override
    public String toString() {
        return String.format("Vector{x=%f, y=%f}", this.coordinate.x, this.coordinate.y);
    }
}
