package net.smackem.jobotwar.lang;

import java.util.Objects;

public final class Instruction {
    private final OpCode opCode;
    private double f64Arg;
    private int intArg;
    private String strArg;

    Instruction(OpCode opCode) {
        this.opCode = opCode;
    }

    Instruction(OpCode opCode, double f64Arg) {
        this.opCode = opCode;
        this.f64Arg = f64Arg;
    }

    Instruction(OpCode opCode, int intArg) {
        this.opCode = opCode;
        this.intArg = intArg;
    }

    Instruction(OpCode opCode, String strArg) {
        this.opCode = opCode;
        this.strArg = strArg;
    }

    public OpCode opCode() {
        return this.opCode;
    }

    public double f64Arg() {
        return this.f64Arg;
    }

    void setF64Arg(double value) {
        this.f64Arg = value;
    }

    public int intArg() {
        return this.intArg;
    }

    void setIntArg(int value) {
        this.intArg = value;
    }

    public String strArg() {
        return this.strArg;
    }

    void setStrArg(String value) {
        this.strArg = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Instruction that = (Instruction) o;
        return Double.compare(that.f64Arg, f64Arg) == 0 &&
                intArg == that.intArg &&
                opCode == that.opCode &&
                Objects.equals(strArg, that.strArg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opCode, f64Arg, intArg, strArg);
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "opCode=" + opCode +
                ", f64Arg=" + f64Arg +
                ", intArg=" + intArg +
                ", strArg='" + strArg + '\'' +
                '}';
    }
}
