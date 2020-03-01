package net.smackem.jobotwar.lang.common;

import net.smackem.jobotwar.lang.Instruction;
import net.smackem.jobotwar.lang.OpCode;
import net.smackem.jobotwar.lang.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Emitter {

    private boolean disabled;
    private List<Instruction> instructions = new ArrayList<>();

    public List<Instruction> instructions() {
        return this.instructions;
    }

    public void setDisabled(boolean value) {
        this.disabled = value;
    }

    public boolean isDisabled() {
        return this.disabled;
    }

    public Program buildProgram() {
        fixup();
        return new Program(instructions());
    }

    public void emit(OpCode opCode) {
        emit(new Instruction(opCode));
    }

    public void emit(OpCode opCode, int intArg) {
        emit(new Instruction(opCode, intArg));
    }

    @SuppressWarnings("SameParameterValue") // opCode is always LD_F64
    public void emit(OpCode opCode, double f64Arg) {
        emit(new Instruction(opCode, f64Arg));
    }

    public void emit(OpCode opCode, String strArg) {
        emit(new Instruction(opCode, strArg));
    }

    private void emit(Instruction instruction) {
        if (this.isDisabled()) {
            return;
        }
        instructions().add(instruction);
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
