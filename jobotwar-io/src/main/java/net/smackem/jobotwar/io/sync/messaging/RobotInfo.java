package net.smackem.jobotwar.io.sync.messaging;

import java.util.Optional;

public class RobotInfo {
    private final String name;
    private final String color;
    private final Double x;
    private final Double y;
    private final Boolean ready;

    private RobotInfo(Builder builder) {
        this.name = builder.name;
        this.color = builder.color;
        this.x = builder.x;
        this.y = builder.y;
        this.ready = builder.ready;
    }

    public static class Builder {
        private String name;
        private String color;
        private Double x;
        private Double y;
        private Boolean ready;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder x(double x) {
            this.x = x;
            return this;
        }

        public Builder y(double y) {
            this.y = y;
            return this;
        }

        public Builder ready(boolean value) {
            this.ready = value;
            return this;
        }

        public RobotInfo build() {
            return new RobotInfo(this);
        }
    }

    public Optional<String> name() {
        return Optional.ofNullable(this.name);
    }

    public Optional<String> color() {
        return Optional.ofNullable(this.color);
    }

    public Optional<Double> x() {
        return Optional.ofNullable(this.x);
    }

    public Optional<Double> y() {
        return Optional.ofNullable(this.y);
    }

    public Optional<Boolean> isReady() {
        return Optional.ofNullable(this.ready);
    }
}
