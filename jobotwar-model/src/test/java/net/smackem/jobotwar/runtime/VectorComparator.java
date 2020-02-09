package net.smackem.jobotwar.runtime;

import java.util.Comparator;

public class VectorComparator implements Comparator<Vector> {
    private final double tolerance;

    public static final double DEFAULT_TOLERANCE = 0.0001;

    public VectorComparator(double tolerance) {
        this.tolerance = tolerance;
    }

    @Override
    public int compare(Vector a, Vector b) {
        final double distance = Vector.distance(a, b);
        if (distance < this.tolerance) {
            return 0;
        }
        return 1;
    }
}
