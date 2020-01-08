package net.smackem.jobotwar.lang;

public enum OpCode {
    LD_F64,     // push f64Arg
    LD_REG,     // push register strArg
    LD_LOC,     // push local intArg
    ST_LOC,     // pop a, store a to local intArg
    ST_REG,     // pop a, store a to register strArg
    ADD,        // pop b, pop a, push a + b
    SUB,        // pop b, pop a, push a - b
    MUL,        // pop b, pop a, push a * b
    DIV,        // pop b, pop a, push a / b
    MOD,        // pop b, pop a, push a % b
    OR,         // pop b, pop a, push a or b (anything but 0.0 is true)
    AND,        // pop b, pop a, push a and b
    LABEL,      // nop, label intArg
    EQ,         // pop b, a, push a = b
    NEQ,        // pop b, a, push a != b
    GT,         // pop b, a, push a > b
    GE,         // pop b, a, push a >= b
    LT,         // pop b, a, push a < b
    LE,         // pop b, a, push a <= b
    BR,         // branch to label intArg
    BR_ZERO,    // pop a, if 0.0 then branch to intArg
    BR_NONZERO, // pop a, if not 0.0 then branch to intArg
    DUP,        // pop a, push a, push a
    ABS,        // pop a, push func(a)
    NOT,        // pop a, push func(a)
    TAN,        // pop a, push func(a)
    SIN,        // pop a, push func(a)
    COS,        // pop a, push func(a)
    ATAN,       // pop a, push func(a)
    ASIN,       // pop a, push func(a)
    ACOS,       // pop a, push func(a)
    SQRT,       // pop a, push func(a)
    CALL,       // push current pc, branch to label intArg
    RET,        // pop a, a -> pc
}
