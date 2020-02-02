package net.smackem.jobotwar.runtime;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class VectorTest {

    private static final double DELTA = 0.0001;

    @Test
    public void getLength() {
        assertThat(new Vector(1, 0).length()).isEqualTo(1);
        assertThat(new Vector(1, 0).length()).isEqualTo(1);
        assertThat(new Vector(0, 2).length()).isEqualTo(2);
        assertThat(new Vector(-2, 0).length()).isEqualTo(2);
    }

    @Test
    public void normalize() {
        assertThat(new Vector(100, 12).normalize().length())
                .isEqualTo(1, offset(DELTA));
        assertThat(new Vector(452, 2436).normalize().length())
                .isEqualTo(1, offset(DELTA));
        assertThat(new Vector(4, -1234).normalize().length())
                .isEqualTo(1, offset(DELTA));
    }

    @Test
    public void add() {
        assertThat(new Vector(1, 2).add(new Vector(2, 3)))
                .isEqualTo(new Vector(3, 5));
    }

    @Test
    public void subtract() {
        assertThat(new Vector(1, 2).subtract(new Vector(2, 3)))
                .isEqualTo(new Vector(-1, -1));
    }

    @Test
    public void multiply() {
        assertThat(new Vector(2, 3).multiply(4))
                .isEqualTo(new Vector(8, 12));
    }

    @Test
    public void divide() {
        assertThat(new Vector(8, 12).divide(4))
                .isEqualTo(new Vector(2, 3));
    }

    @Test
    public void negate() {
        assertThat(new Vector(2, 3).negate())
                .isEqualTo(new Vector(-2, -3));
    }

    @Test
    public void distance() {
        assertThat(Vector.distance(new Vector(0, 0), new Vector(100, 0)))
                .isEqualTo(100, offset(DELTA));
        assertThat(Vector.distance(new Vector(-50, 0), new Vector(50, 0)))
                .isEqualTo(100, offset(DELTA));
    }

    @Test
    public void fromAngleAndLength() {
        final Vector a = new Vector(100, 100);
        assertThat(a.add(Vector.fromAngleAndLength(0, 100))).isEqualTo(
                new Vector(200, 100));
    }
}