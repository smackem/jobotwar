package net.smackem.jobotwar.lang;

public enum OpCode {
    PUSH_F64,   // push f64Arg
    PUSH_REG,   // push register strArg
    PUSH_LOC,   // push local intArg
    ST_LOC,     // pop a, store a to local intArg
    ST_REG,     // pop a, store a to register strArg
    ADD,        // pop b, pop a, push a + b
    SUB,        // pop b, pop a, push a - b
    MUL,        // pop b, pop a, push a * b
    DIV,        // pop b, pop a, push a / b
    OR,         // pop b, pop a, push a or b (anything but 0.0 is true)
    AND,        // pop b, pop a, push a and b
    LABEL,      // nop, label intArg
    BR,         // branch to label intArg
    BR_ZERO,    // pop a, if 0.0 then branch to intArg
}
