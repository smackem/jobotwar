package net.smackem.jobotwar.runtime;

public class Constants {
    private Constants() {
        throw new IllegalAccessError();
    }

    public static int MAX_SHOT = 10_000;
    public static double MAX_RANGE = 10_000;
    public static int MAX_BOARD_WIDTH  = 5_000;
    public static int MAX_BOARD_HEIGHT  = 5_000;
    public static double ANGLE_PRECISION = 0.0001;
    public static int MAX_HEALTH = 100;
    public static double MAX_ROBOT_ACCELERATION = 10;
    public static double MAX_ROBOT_SPEED = 20;
    public static double PROJECTILE_SPEED = 8;
    public static double ROBOT_RADIUS = 10;
    public static double EXPLOSION_RADIUS = 20;
}
