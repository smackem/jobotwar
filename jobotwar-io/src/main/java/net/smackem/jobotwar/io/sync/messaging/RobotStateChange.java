package net.smackem.jobotwar.io.sync.messaging;

import java.util.Optional;

/**
 * "state": {
 *     "radar": 123.34,
 *     "aim": 123.34,
 *     "random": 4711.42,
 *     "shot": 1000.0,
 *     "speedX": 54.3,
 *     "speedY": 34.2
 * }
 */
public class RobotStateChange {
    private final Double radar;
    private final Double aim;
    private final Double random;
    private final Double shot;
    private final Double speedX;
    private final Double speedY;

    private RobotStateChange(Builder builder) {
        this.radar = builder.radar;
        this.aim = builder.aim;
        this.random = builder.random;
        this.shot = builder.shot;
        this.speedX = builder.speedX;
        this.speedY = builder.speedY;
    }

    public static class Builder {
        private Double radar;
        private Double aim;
        private Double random;
        private Double shot;
        private Double speedX;
        private Double speedY;

        public Builder radar(double value) {
            this.radar = value;
            return this;
        }

        public Builder aim(double value) {
            this.aim = value;
            return this;
        }

        public Builder random(double value) {
            this.random = value;
            return this;
        }

        public Builder shot(double value) {
            this.shot = value;
            return this;
        }

        public Builder speedX(double value) {
            this.speedX = value;
            return this;
        }

        public Builder speedY(double value) {
            this.speedY = value;
            return this;
        }

        public RobotStateChange build() {
            return new RobotStateChange(this);
        }
    }

    public Optional<Double> radar() {
        return Optional.ofNullable(this.radar);
    }

    public Optional<Double> aim() {
        return Optional.ofNullable(this.aim);
    }

    public Optional<Double> random() {
        return Optional.ofNullable(this.random);
    }

    public Optional<Double> shot() {
        return Optional.ofNullable(this.shot);
    }

    public Optional<Double> speedX() {
        return Optional.ofNullable(this.speedX);
    }

    public Optional<Double> speedY() {
        return Optional.ofNullable(this.speedY);
    }
}
