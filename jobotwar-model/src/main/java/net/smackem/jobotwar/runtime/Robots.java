package net.smackem.jobotwar.runtime;

/**
 * Non-instantiable class that provides utility methods which operate on {@link Robot}s.
 */
class Robots {
    private Robots() { throw new IllegalAccessError(); }

    /**
     * Builds a new, 100% health {@link Robot} that has the same program, built-in parameters and position
     * as the specified {@link Robot}. The damage state is NOT copied.
     * The passed robot has to be controlled by a {@link CompiledProgram}.
     * @param robot The original {@link Robot} to use as template.
     * @param ctx The new context to hook in.
     *            If {@code null}, the original messageLogger is also used for the new robot.
     * @return A new instance of {@link Robot}.
     */
    public static Robot fromTemplate(Robot robot, RobotProgramContext ctx) {
        return new Robot.Builder(r -> cloneProgram(robot.program(), r, ctx))
                .name(robot.name())
                .acceleration(robot.acceleration())
                .imageUrl(robot.imageUrl())
                .rgb(robot.rgb())
                .x(robot.getX())
                .y(robot.getY())
                .coolDownTicks(robot.coolDownTicks())
                .build();
    }

    private static RobotProgram cloneProgram(RobotProgram original, Robot robot, RobotProgramContext ctx) {
        if (original instanceof CompiledProgram == false) {
            throw new IllegalArgumentException("only CompiledProgram can be cloned!");
        }
        final CompiledProgram compiledProgram = (CompiledProgram)original;
        return new CompiledProgram(robot,
                compiledProgram.program(),
                ctx != null ? ctx : compiledProgram.context());
    }
}
