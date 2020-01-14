package net.smackem.jobotwar.lang;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * A compiled program.
 */
public final class Program {
    private final List<Instruction> instructions;

    /**
     * Initializes a new instance of {@link Program}.
     * @param instructions The emitted instructions.
     */
    Program(List<Instruction> instructions) {
        this.instructions = Objects.requireNonNull(instructions);
    }

    /**
     * @return The emitted instructions that make up the program.
     */
    Collection<Instruction> instructions() {
        return this.instructions;
    }
}
