package net.smackem.jobotwar.lang;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class Program {
    private final List<Instruction> instructions;

    Program(List<Instruction> instructions) {
        this.instructions = Objects.requireNonNull(instructions);
    }

    public Collection<Instruction> instructions() {
        return this.instructions;
    }
}
