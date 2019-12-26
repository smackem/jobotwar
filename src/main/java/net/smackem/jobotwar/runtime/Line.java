package net.smackem.jobotwar.runtime;

public final class Line {
    private final Vector p1;
    private final Vector p2;

    public Line(Vector p1, Vector p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public static Line fromAngleAndLength(Vector p1, double angle, double length) {
        return new Line(
                p1,
                new Vector(Math.cos(angle) * length, Math.sin(angle) * length));
    }

    public Vector getPoint1() {
        return this.p1;
    }

    public Vector getPoint2() {
        return this.p2;
    }

    public double distanceFromPoint(Vector point) {
        throw new RuntimeException();
    }

    public static Vector intersect(Line line1, Line line2) {
        throw new RuntimeException();
    }
}
