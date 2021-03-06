package net.smackem.jobotwar.runtime;

import com.google.common.base.Strings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class RuntimeProgram implements RobotProgram {
    private final List<Instruction> instructions;
    private final Map<String, Integer> labels;
    private final Robot robot;
    private int pc;

    public RuntimeProgram(Robot robot, Instruction... instructions) {
        this.robot = robot;
        this.instructions = Arrays.asList(instructions);
        this.labels = new HashMap<>();
        int index = 0;
        for (final Instruction instruction : instructions) {
            if (Strings.isNullOrEmpty(instruction.label) == false) {
                labels.put(instruction.label, index);
            }
            index++;
        }
    }

    @Override
    public boolean next() {
        if (this.pc >= this.instructions.size()) {
            return false;
        }

        final String target = this.instructions.get(this.pc).function.apply(this.robot);
        if (Strings.isNullOrEmpty(target) == false) {
            this.pc = this.labels.get(target);
        } else {
            this.pc++;
        }

        return this.pc < this.instructions.size();
    }

    public static Instruction instruction(String label, Function<Robot, String> function) {
        return new Instruction(label, function);
    }

    @SuppressWarnings("WeakerAccess") // used in unit tests
    public static class Instruction {
        private final String label;
        private final Function<Robot, String> function;

        private Instruction(String label, Function<Robot, String> function) {
            this.label = label;
            this.function = function;
        }
    }
}
