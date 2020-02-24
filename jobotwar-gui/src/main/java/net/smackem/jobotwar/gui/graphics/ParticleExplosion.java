package net.smackem.jobotwar.gui.graphics;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import net.smackem.jobotwar.util.Arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

class ParticleExplosion {
    private final Collection<Dot> dots;
    private final double width, height;
    private static final int OPACITY_STEPS = 20;
    private static final List<Color[]> PRECOMPUTED_COLORS;

    static {
        PRECOMPUTED_COLORS = new ArrayList<>(360);
        for (int hue = 0; hue < 360; hue++) {
            final Color hsb = Color.hsb(hue, 1, 1);
            final Color[] colors = new Color[OPACITY_STEPS + 1];
            for (int opacity = 0; opacity <= OPACITY_STEPS; opacity++) {
                colors[opacity] = Color.color(hsb.getRed(), hsb.getGreen(), hsb.getBlue(),
                        opacity / (double)OPACITY_STEPS);
            }
            PRECOMPUTED_COLORS.add(colors);
        }
    }

    public ParticleExplosion(double width, double height) {
        this.width = width;
        this.height = height;
        this.dots = createDots(width, height, 3_000, 0, 360);
    }

    public ParticleExplosion(double width, double height, int dotCount, int minHue, int maxHue) {
        Arguments.requireRange(dotCount, 0, 10_000);
        if (minHue >= maxHue) {
            throw new IllegalArgumentException("minHue must be less than maxHue");
        }
        this.width = width;
        this.height = height;
        this.dots = createDots(width, height, dotCount, minHue, maxHue);
    }

    public double width() {
        return this.width;
    }

    public double height() {
        return this.height;
    }

    /**
     * Renders the explosion to the specified {@link GraphicsContext}.
     * @param gc The {@link GraphicsContext} to render to.
     * @return {@code false} if the animation is finished, otherwise {@code true}.
     */
    public boolean render(GraphicsContext gc) {
        boolean finished = true;

        for (final Dot dot : this.dots) {
            if (dot.finished) {
                continue;
            }
            final Color color = PRECOMPUTED_COLORS.get(dot.hue)[(int)(dot.distance * OPACITY_STEPS / dot.initialDistance)];
            gc.setFill(color);
            gc.fillOval(dot.pos.getX(), dot.pos.getY(), 3, 3);
            finished &= dot.move();
        }

        return finished == false;
    }

    private static Collection<Dot> createDots(double width, double height, int dotCount, int minHue, int maxHue) {
        final Random random = new Random();
        final double maxRadius = width * 3 / 4;
        final Point2D center = new Point2D(width / 2, height / 2);
        final Collection<Dot> dots = new ArrayList<>(dotCount);

        for ( ; dotCount > 0; dotCount--) {
            final double angle = random.nextDouble() * Math.PI * 2;
            final double radius = random.nextDouble() * maxRadius;
            final Point2D pos = new Point2D(
                    center.getX() + 12 - random.nextDouble() * 24,
                    center.getY() + 12 - random.nextDouble() * 24);
            final Point2D dest = new Point2D(
                    center.getX() + Math.cos(angle) * radius,
                    center.getY() + Math.sin(angle) * radius);
            final double speed = 0.6 + random.nextDouble() * 8.4;
            final int hue = clampHue(minHue + random.nextInt(maxHue - minHue));
            dots.add(new Dot(pos, dest, speed, hue));
        }

        return dots;
    }

    private static int clampHue(int hue) {
        if (hue < 0) {
            return hue + 360;
        }
        if (hue > 360) {
            return hue - 360;
        }
        return hue;
    }

    private static class Dot {
        final Point2D dest;
        final Point2D inc;
        final int hue;
        final double initialDistance;
        final double speed;
        double distance;
        Point2D pos;
        boolean finished;

        Dot(Point2D pos, Point2D dest, double speed, int hue) {
            final Point2D difference = dest.subtract(pos);
            this.dest = dest;
            this.inc = difference.normalize().multiply(speed);
            this.pos = pos;
            this.initialDistance = difference.magnitude();
            this.speed = speed;
            this.hue = hue;
            this.distance = this.initialDistance;
        }

        boolean move() {
            if (this.finished) {
                return true;
            }
            this.pos = this.pos.add(this.inc);
            this.distance = dest.subtract(pos).magnitude();
            this.finished = this.distance < this.speed;
            return this.finished;
        }
    }
}
