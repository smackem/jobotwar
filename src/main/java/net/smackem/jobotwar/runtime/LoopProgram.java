package net.smackem.jobotwar.runtime;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class LoopProgram implements RobotProgram {
    private final List<Consumer<Robot>> instructions;
    private int index;

    public static final LoopProgram EMPTY = new LoopProgram();

    @SafeVarargs
    public LoopProgram(Consumer<Robot>... instructions) {
        this.instructions = Arrays.asList(instructions);
    }

    @Override
    public boolean next(Robot robot) {
        this.instructions.get(this.index).accept(robot);
        this.index++;
        if (this.index >= this.instructions.size()) {
            this.index = 0;
        }
        return true;
    }
}
