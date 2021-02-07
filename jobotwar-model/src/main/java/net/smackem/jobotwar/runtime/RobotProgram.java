package net.smackem.jobotwar.runtime;

/**
 * Implemented by classes that hold the executable control logic of a {@link Robot}.
 */
public interface RobotProgram {
    /**
     * Advances the program up to a state defined by the implementing class (e.g. one instruction).
     *
     * @return {@code true} if the program has more instructions to execute, {@code false} if
     *      it has ended.
     * @throws RobotProgramException There was a runtime error within the executing program.
     */
    boolean next() throws RobotProgramException;
}
