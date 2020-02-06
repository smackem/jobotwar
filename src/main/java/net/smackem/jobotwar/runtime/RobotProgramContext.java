package net.smackem.jobotwar.runtime;

public interface RobotProgramContext {
    /**
     * Logs the message.
     * @param robot The {@link Robot} that produced the message.
     * @param category The category (e.g. register name)
     * @param value The new value of the item identified by {@code category}.
     */
    void logMessage(Robot robot, String category, double value);

    /**
     * Supplies a random double in the range 0..1.
     * @param robot The robot that consumes the random number.
     * @return A new random number.
     */
    double nextRandomDouble(Robot robot);
}
