package net.smackem.jobotwar.runtime;

/**
 * Thrown when a {@link RobotProgram} encounters an execution error.
 * {@link #getCause()} returns an error specific for the program implementation.
 */
public class RobotProgramException extends Exception {

    private final String robotName;

    /**
     * Initializes a new instance of {@link RobotProgramException}.
     */
    public RobotProgramException(String robotName, String message, Throwable cause) {
        super(message, cause);
        this.robotName = robotName;
    }

    /**
     * @return The name of the robot with the faulted program.
     */
    public String robotName() {
        return this.robotName;
    }
}
