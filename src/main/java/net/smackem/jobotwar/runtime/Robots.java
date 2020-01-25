package net.smackem.jobotwar.runtime;

public class Robots {
    private Robots() { throw new IllegalAccessError(); }

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
