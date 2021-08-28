package net.smackem.jobotwar.runtime;

import java.time.Duration;

public class Constants {
    private Constants() {
        throw new IllegalAccessError();
    }

    public static final String GAME_VERSION = "1.0.0";
    public static final int MAX_SHOT = 10_000;
    public static final double MAX_RADAR_RANGE = 10_000;
    public static final int MAX_BOARD_WIDTH  = 5_000;
    public static final int MAX_BOARD_HEIGHT  = 5_000;
    public static final double ANGLE_PRECISION = 0.000_1;
    public static final int MAX_HEALTH = 100;
    public static final double MAX_ROBOT_ACCELERATION = 8;
    public static final double DEFAULT_ROBOT_ACCELERATION = 0.5;
    public static final double MAX_ROBOT_SPEED = 8;
    public static final double PROJECTILE_SPEED = 17;
    public static final double ROBOT_RADIUS = 18;
    public static final double EXPLOSION_RADIUS = 25;
    public static final int DEFAULT_COOL_DOWN_TICKS = 20;
    public static final double MAX_ROBOT_GAME_SPEED = 500;
    public static final Duration TICK_DURATION = Duration.ofMillis(40);
}
