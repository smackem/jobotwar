package net.smackem.jobotwar.runtime;

/**
 * A geometrical line between two points (expressed as {@link Vector}s.
 */
public final class Line {
    private final Vector p1;
    private final Vector p2;

    /**
     * Initializes a new instance of {@link Line}.
     * @param p1 The first point.
     * @param p2 The second point.
     */
    public Line(Vector p1, Vector p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    /**
     * Creates a new {@link Line} from a starting point, an angle and a length.
     * @param p1 The starting point
     * @param angle The angle in radians.
     * @param length The new line's length.
     * @return A new instance of {@link Line}.
     */
    public static Line fromAngleAndLength(Vector p1, double angle, double length) {
        return new Line(
                p1,
                new Vector(Math.cos(angle) * length, Math.sin(angle) * length));
    }

    /**
     * @return The first point of the line.
     */
    public Vector getPoint1() {
        return this.p1;
    }

    /**
     * @return The second point of the line.
     */
    public Vector getPoint2() {
        return this.p2;
    }

    /**
     * @return The length of the line.
     */
    public double length() {
        return Vector.distance(this.p1, this.p2);
    }

    /**
     * Calculates the shortest (perpendicular) distance from the given point to the line.
     * @param point The point.
     * @return The perpendicular distance from the given point to the line.
     */
    public double distanceFromPoint(Vector point) {
        final double x1 = this.p1.getX();
        final double y1 = this.p1.getY();
        final double x2 = this.p2.getX();
        final double y2 = this.p2.getY();
        final double x0 = point.getX();
        final double y0 = point.getY();
        final double dx = x2 - x1;
        final double dy = y2 - y1;
        // denominator is line.length, but inline it here for performance
        return Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2*y1 - y2*x1) / Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * Intersects two {@link Line}s and returns the intersection point if any.
     * @param line1 The first line.
     * @param line2 The second line.
     * @return The point where the two lines intersect or {@code null} if they are in parallel.
     */
    public static Vector intersect(Line line1, Line line2) {
        final Vector p1 = line1.p1, p2 = line1.p2;
        final Vector p3 = line2.p1, p4 = line2.p2;
        final double x1 = p1.getX(), y1 = p1.getY();
        final double x2 = p2.getX(), y2 = p2.getY();
        final double x3 = p3.getX(), y3 = p3.getY();
        final double x4 = p4.getX(), y4 = p4.getY();

        if (x1 == x2) {
            return intersectVertical(x3, y3, x4, y4, x1);
        }
        if (x3 == x4) {
            return intersectVertical(x1, y1, x2, y2, x3);
        }

        final double m1 = (y2 - y1) / (x2 - x1);
        final double m2 = (y4 - y3) / (x4 - x3);

        if (m1 == m2) {
            // the lines are parallel
            return null;
        }

        final double x = (m1*x1 - m2*x3 + y3 - y1) / (m1 - m2);
        return new Vector(x, (x-x1) * m1 + y1);
    }

    private static Vector intersectVertical(double x1, double y1, double x2, double y2, double x) {
        if (x1 == x2) {
            // line is parallel to y axis
            return null;
        }
        return new Vector(x, (x - x1) * (y2 - y1) / (x2 - x1) + y1);
    }
}
