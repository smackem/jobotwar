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
        if (this.index >= this.instructions.size()) {
            return false;
        }
        this.instructions.get(this.index).accept(robot);
        this.index++;
        return this.index < this.instructions.size();
    }
}
