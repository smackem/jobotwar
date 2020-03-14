package net.smackem.jobotwar.lang.common;

import net.smackem.jobotwar.lang.Instruction;
import net.smackem.jobotwar.lang.OpCode;
import net.smackem.jobotwar.lang.Program;

import java.util.*;

/**
 * Emitter is used to emit byte code.
 */
public class Emitter {

    private boolean disabled;
    private List<Instruction> instructions = new ArrayList<>();

    /**
     * @return an unmodifiable list containing the {@link Instruction}s that have been emitted.
     */
    public List<Instruction> instructions() {
        return Collections.unmodifiableList(this.instructions);
    }

    /**
     * Disables or enables the {@link Emitter}. If disabled, the {@code emit} functions
     * do nothing.
     * @param value {@code true} to disable the emitter, {@code false} to enable it.
     */
    public void setDisabled(boolean value) {
        this.disabled = value;
    }

    /**
     * @return A value indicating whether the emitter is currently disabled.
     */
    public boolean isDisabled() {
        return this.disabled;
    }

    /**
     * Builds a {@link Program} from the emitted {@link #instructions()}.
     * @return A new {@link Program}.
     */
    public Program buildProgram() {
        fixup();
        return new Program(instructions());
    }

    /**
     * Emits an instruction without argument.
     * @param opCode The {@link OpCode} of the instruction to emit.
     */
    public void emit(OpCode opCode) {
        emit(new Instruction(opCode));
    }

    /**
     * Emits an instruction with an integer argument.
     * @param opCode The {@link OpCode} of the instruction to emit.
     * @param intArg The integer argument.
     */
    public void emit(OpCode opCode, int intArg) {
        emit(new Instruction(opCode, intArg));
    }

    /**
     * Emits an instruction with a floating point argument.
     * @param opCode The {@link OpCode} of the instruction to emit.
     * @param f64Arg The floating point argument.
     */
    @SuppressWarnings("SameParameterValue") // opCode is always LD_F64
    public void emit(OpCode opCode, double f64Arg) {
        emit(new Instruction(opCode, f64Arg));
    }

    /**
     * Emits an instruction with a string argument.
     * @param opCode The {@link OpCode} of the instruction to emit.
     * @param strArg The string argument.
     */
    public void emit(OpCode opCode, String strArg) {
        emit(new Instruction(opCode, strArg));
    }

    /**
     * Emits an instruction, inserting it immediately before the last instruction.
     * @param instruction The {@link Instruction} to emit.
     */
    public void emitBeforeLast(Instruction instruction) {
        if (this.instructions.isEmpty()) {
            throw new UnsupportedOperationException("operation not supported on empty instruction list");
        }
        if (this.isDisabled()) {
            return;
        }
        this.instructions.add(instructions.size() - 1, instruction);
    }

    private void emit(Instruction instruction) {
        if (this.isDisabled()) {
            return;
        }
        this.instructions.add(instruction);
    }

    private void fixup() {
        final Map<String, Integer> labelIndices = new HashMap<>();
        int index = 0;
        for (final Instruction instr : this.instructions) {
            if (instr.opCode() == OpCode.LABEL) {
                labelIndices.put(instr.strArg(), index);
                instr.setIntArg(index);
            }
            index++;
        }
        this.instructions().stream()
                .filter(instr -> instr.opCode().isBranch() && instr.strArg() != null)
                .forEach(instr -> {
                    final Integer target = labelIndices.get(instr.strArg());
                    if (target == null) {
                        throw new RuntimeException("Unknown label: " + instr.strArg());
                    }
                    instr.setIntArg(target);
                });
    }
}
