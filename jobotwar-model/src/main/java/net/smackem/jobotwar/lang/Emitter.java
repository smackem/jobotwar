package net.smackem.jobotwar.lang;

import net.smackem.jobotwar.lang.v1.JobotwarV1BaseListener;

import java.util.*;

public class Emitter extends JobotwarV1BaseListener {
    private final List<Instruction> instructions = new ArrayList<>();
    private boolean disabled;

    public final Program buildProgram() {
        this.fixup();
        return new Program(instructions());
    }

    protected List<Instruction> instructions() {
        return Collections.unmodifiableList(this.instructions);
    }

    protected void setDisabled(boolean value) {
        this.disabled = value;
    }

    protected void emit(OpCode opCode) {
        emit(new Instruction(opCode));
    }

    protected void emit(OpCode opCode, int intArg) {
        emit(new Instruction(opCode, intArg));
    }

    @SuppressWarnings("SameParameterValue") // opCode is always LD_F64
    protected void emit(OpCode opCode, double f64Arg) {
        emit(new Instruction(opCode, f64Arg));
    }

    protected void emit(OpCode opCode, String strArg) {
        emit(new Instruction(opCode, strArg));
    }

    private void emit(Instruction instruction) {
        if (this.disabled) {
            return;
        }
        this.instructions.add(instruction);
    }

    private void fixup() {
        final Map<String, Integer> labelIndices = new HashMap<>();
        int index = 0;
        for (final Instruction instr : this.instructions()) {
            if (instr.opCode() == OpCode.LABEL) {
                labelIndices.put(instr.strArg(), index);
                instr.setIntArg(index);
            }
            index++;
        }
        this.instructions().stream()
                .filter(instr -> instr.opCode().isBranch())
                .forEach(instr -> {
                    final Integer target = labelIndices.get(instr.strArg());
                    if (target == null) {
                        throw new RuntimeException("Unknown label: " + instr.strArg());
                    }
                    instr.setIntArg(target);
                });
    }
}
