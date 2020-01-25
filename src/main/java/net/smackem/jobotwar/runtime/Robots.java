package net.smackem.jobotwar.runtime;

/**
 * Non-instantiable class that provides utility methods which operate on {@link Robot}s.
 */
public class Robots {
    private Robots() { throw new IllegalAccessError(); }

    /**
     * Builds a {@link Robot} that has the same program and parameters as the specified {@link Robot}.
     * The passed robot has to be controlled by a {@link CompiledProgram}.
     * @param robot The original {@link Robot} to use as template.
     * @return A new instance of {@link Robot}.
     */
    public static Robot buildLike(Robot robot) {
        return new Robot.Builder(r -> cloneProgram(robot.program(), r))
                .name(robot.name())
                .acceleration(robot.acceleration())
                .imageUrl(robot.imageUrl())
                .rgb(robot.rgb())
                .x(robot.getX())
                .y(robot.getY())
                .coolDownTicks(robot.coolDownTicks())
                .build();
    }

    private static RobotProgram cloneProgram(RobotProgram original, Robot robot) {
        if (original instanceof CompiledProgram == false) {
            throw new IllegalArgumentException("only CompiledProgram can be cloned!");
        }
        final CompiledProgram compiledProgram = (CompiledProgram)original;
        return new CompiledProgram(robot, compiledProgram.program(), compiledProgram.messageLogger());
    }
}
