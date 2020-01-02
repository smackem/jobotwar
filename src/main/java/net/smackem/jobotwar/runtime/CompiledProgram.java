package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.lang.Program;

public class CompiledProgram implements RobotProgram {

    private final Robot robot;
    private final Program program;

    private CompiledProgram(Robot robot, Program program) {
        this.robot = robot;
        this.program = program;
    }

    @Override
    public boolean next() {
        return false;
    }
}
