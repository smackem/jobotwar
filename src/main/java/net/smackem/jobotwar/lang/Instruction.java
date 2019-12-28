package net.smackem.jobotwar.lang;

public final class Instruction {
    private final OpCode opCode;
    private final double f64Arg;
    private final int intArg;
    private final String strArg;

    public Instruction(OpCode opCode, double f64Arg) {
        this.opCode = opCode;
        this.f64Arg = f64Arg;
        this.intArg = 0;
        this.strArg = null;
    }

    public Instruction(OpCode opCode, int intArg) {
        this.opCode = opCode;
        this.intArg = intArg;
        this.f64Arg = 0.0;
        this.strArg = null;
    }

    public Instruction(OpCode opCode, String strArg) {
        this.opCode = opCode;
        this.strArg = strArg;
        this.f64Arg = 0.0;
        this.intArg = 0;
    }

    public OpCode getOpCode() {
        return this.opCode;
    }

    public double getF64Arg() {
        return this.f64Arg;
    }

    public int getIntArg() {
        return this.intArg;
    }

    public String getStrArg() {
        return this.strArg;
    }
}
