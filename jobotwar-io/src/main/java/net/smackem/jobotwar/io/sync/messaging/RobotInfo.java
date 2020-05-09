package net.smackem.jobotwar.io.sync.messaging;

import java.util.Optional;

public class RobotInfo {
    private final String name;
    private final String color;
    private final Double x;
    private final Double y;

    private RobotInfo(Builder builder) {
        this.name = builder.name;
        this.color = builder.color;
        this.x = builder.x;
        this.y = builder.y;
    }

    public static class Builder {
        private String name;
        private String color;
        private Double x;
        private Double y;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder x(Double x) {
            this.x = x;
            return this;
        }

        public Builder y(Double y) {
            this.y = y;
            return this;
        }

        public RobotInfo build() {
            return new RobotInfo(this);
        }
    }

    public String name() {
        return name;
    }

    public String color() {
        return color;
    }

    public Optional<Double> x() {
        return Optional.ofNullable(x);
    }

    public Optional<Double> y() {
        return Optional.ofNullable(y);
    }
}
