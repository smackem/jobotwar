package net.smackem.jobotwar.runtime;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RobotProgram {
    private final List<Consumer<Robot>> instructions;
    private int index;

    public static final RobotProgram EMPTY = new RobotProgram();

    @SafeVarargs
    public RobotProgram(Consumer<Robot>... instructions) {
        this.instructions = Arrays.asList(instructions);
    }

    public boolean next(Robot robot) {
        this.instructions.get(this.index).accept(robot);
        this.index++;
        if (this.index >= this.instructions.size()) {
            this.index = 0;
        }
        return true;
    }
}
