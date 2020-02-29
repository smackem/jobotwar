package net.smackem.jobotwar.lang;

public enum OpCode {
    LD_F64(false),     // push f64Arg
    LD_REG(false),     // push register strArg
    LD_LOC(false),     // push local intArg
    LD_GLB(false),     // push global intArg
    ST_LOC(false),     // pop a, store a to local intArg
    ST_REG(false),     // pop a, store a to register strArg
    ST_GLB(false),     // pop a, store a to global intArg
    ADD(false),        // pop b, pop a, push a + b
    SUB(false),        // pop b, pop a, push a - b
    MUL(false),        // pop b, pop a, push a * b
    DIV(false),        // pop b, pop a, push a / b
    MOD(false),        // pop b, pop a, push a % b
    OR(false),         // pop b, pop a, push a or b (anything but 0.0 is true)
    AND(false),        // pop b, pop a, push a and b
    LABEL(false),      // nop, label intArg
    EQ(false),         // pop b, a, push a = b
    NEQ(false),        // pop b, a, push a != b
    GT(false),         // pop b, a, push a > b
    GE(false),         // pop b, a, push a >= b
    LT(false),         // pop b, a, push a < b
    LE(false),         // pop b, a, push a <= b
    BR(true),          // branch to label intArg
    BR_ZERO(true),     // pop a, if 0.0 then branch to intArg
    DUP(false),        // pop a, push a, push a
    NOT(false),        // pop a push a != 0
    INVOKE(false),     // pop a, push BuiltinFunc:strArg(a)
    CALL(true),        // pop no. of arguments, push current pc, branch to label intArg
    RET(false),        // pop a, a -> pc
    RET_VAL(false),    // pop v, pop a, a -> pc, push v
    LOG(false);        // pop a, print 'strArg: a'

    private final boolean branch;

    OpCode(boolean branch) {
        this.branch = branch;
    }

    public boolean isBranch() {
        return this.branch;
    }
}
