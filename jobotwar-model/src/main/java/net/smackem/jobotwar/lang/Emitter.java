package net.smackem.jobotwar.lang;

import net.smackem.jobotwar.lang.v1.JobotwarV1BaseListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Emitter extends JobotwarV1BaseListener {
    private final List<Instruction> instructions = new ArrayList<>();
    private boolean disabled;

    public final List<Instruction> instructions() {
        return Collections.unmodifiableList(this.instructions);
    }

    protected boolean isDisabled() {
        return this.disabled;
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

    protected void emit(OpCode opCode, double f64Arg) {
        emit(new Instruction(opCode, f64Arg));
    }

    protected void emit(OpCode opCode, String strArg) {
        emit(new Instruction(opCode, strArg));
    }

    private void emit(Instruction instruction) {
        if (isDisabled()) {
            return;
        }
        this.instructions.add(instruction);
    }
}
