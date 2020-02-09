package net.smackem.jobotwar.runtime;

import java.time.Duration;

public class Constants {
    private Constants() {
        throw new IllegalAccessError();
    }

    public static int MAX_SHOT = 10_000;
    public static double MAX_RADAR_RANGE = 10_000;
    public static int MAX_BOARD_WIDTH  = 5_000;
    public static int MAX_BOARD_HEIGHT  = 5_000;
    public static double ANGLE_PRECISION = 0.000_1;
    public static int MAX_HEALTH = 100;
    public static double MAX_ROBOT_ACCELERATION = 8;
    public static double DEFAULT_ROBOT_ACCELERATION = 0.5;
    public static double MAX_ROBOT_SPEED = 8;
    public static double PROJECTILE_SPEED = 15;
    public static double ROBOT_RADIUS = 18;
    public static double EXPLOSION_RADIUS = 25;
    public static int DEFAULT_COOL_DOWN_TICKS = 20;
    public static double MAX_ROBOT_GAME_SPEED = 500;
    public static Duration TICK_DURATION = Duration.ofMillis(40);
}
