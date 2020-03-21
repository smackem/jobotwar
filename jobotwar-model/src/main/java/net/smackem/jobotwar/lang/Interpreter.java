package net.smackem.jobotwar.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Objects;

/**
 * Executes a compiled {@link Program}.
 */
public final class Interpreter {
    private static final Logger log = LoggerFactory.getLogger(Interpreter.class);
    private final Program program;
    private final List<Instruction> code;
    private final RuntimeEnvironment runtime;
    private final java.util.Stack<Integer> stackFrames = new java.util.Stack<>();//
    private final Stack stack = new Stack();
    private boolean registerStored;
    private boolean yield;
    private int pc;

    /**
     * Initializes a new instance of {@link Interpreter}.
     * @param program The {@link Program} to execute.
     * @param runtime The {@link RuntimeEnvironment} that provides access to
     *                robot registers and sensors.
     */
    public Interpreter(Program program, RuntimeEnvironment runtime) {
        this.program = Objects.requireNonNull(program);
        this.code = new ArrayList<>(program.instructions());
        this.runtime = Objects.requireNonNull(runtime);
    }

    /**
     * @return The {@link Program} to execute.
     */
    public Program program() {
        return this.program;
    }

    /**
     * @return The {@link RuntimeEnvironment} that provides access to robot registers and sensors.
     */
    public RuntimeEnvironment runtime() {
        return this.runtime;
    }

    /**
     * Executes the next program step.
     * @return {@code true} if the program is not at its end, {@code false} if it has executed completely
     * @throws StackException in the case of a stack underflow or overflow.
     */
    public boolean runNext() throws StackException {
        final int codeSize = this.code.size();
        while (this.pc < codeSize) {
            final int target;
            final Instruction instr = this.code.get(this.pc);
            try {
                target = executeInstruction(instr);
            } catch (StackException e) {
                e.pc = this.pc;
                e.instruction = instr;
                throw e;
            } catch (EmptyStackException e) {
                final StackException se = new StackException("Return without stack frame");
                se.pc = this.pc;
                se.instruction = instr;
                throw se;
            }
            if (target >= 0) {
                this.pc = target;
            } else {
                this.pc++;
            }
            if (this.yield) {
                return true;
            }
        }
        return false;
    }

    private int stackFrameOffset() {
        return this.stackFrames.size() > 0 ? this.stackFrames.peek() : 0;
    }

    private int executeInstruction(Instruction instr) throws StackException {
        this.yield = false;
        switch (instr.opCode()) {
            case LD_F64 -> this.stack.push(instr.f64Arg());
            case LD_REG -> this.stack.push(loadRegister(instr.strArg()));
            case LD_LOC -> this.stack.push(this.stack.get(stackFrameOffset() + instr.intArg()));
            case LD_GLB -> this.stack.push(this.stack.get(instr.intArg()));
            case ST_GLB -> {
                final double right = this.stack.pop();
                this.stack.set(instr.intArg(), right);
            }
            case ST_LOC -> {
                final double right = this.stack.pop();
                this.stack.set(stackFrameOffset() + instr.intArg(), right);
            }
            case ST_REG -> {
                final double right = this.stack.pop();
                storeRegister(instr.strArg(), right);
                this.registerStored = true;
            }
            case ADD -> this.stack.push(this.stack.pop() + this.stack.pop());
            case SUB -> {
                final double right = this.stack.pop();
                this.stack.push(this.stack.pop() - right);
            }
            case MUL -> this.stack.push(this.stack.pop() * this.stack.pop());
            case DIV -> {
                final double right = this.stack.pop();
                this.stack.push(this.stack.pop() / right);
            }
            case MOD -> {
                final double right = this.stack.pop();
                this.stack.push(this.stack.pop() % right);
            }
            case OR -> {
                final double right = this.stack.pop();
                this.stack.push(toDouble(toBool(this.stack.pop()) || toBool(right)));
            }
            case AND -> {
                final double right = this.stack.pop();
                this.stack.push(toDouble(toBool(this.stack.pop()) && toBool(right)));
            }
            case EQ -> this.stack.push(toDouble(this.stack.pop() == this.stack.pop()));
            case NEQ -> this.stack.push(toDouble(this.stack.pop() != this.stack.pop()));
            case GT -> {
                final double right = this.stack.pop();
                this.stack.push(toDouble(this.stack.pop() > right));
            }
            case GE -> {
                final double right = this.stack.pop();
                this.stack.push(toDouble(this.stack.pop() >= right));
            }
            case LT -> {
                final double right = this.stack.pop();
                this.stack.push(toDouble(this.stack.pop() < right));
            }
            case LE -> {
                final double right = this.stack.pop();
                this.stack.push(toDouble(this.stack.pop() <= right));
            }
            case LABEL -> {
                if (this.registerStored) {
                    this.registerStored = false;
                    this.yield = true;
                }
            }
            case BR -> {
                this.yield = true;
                return instr.intArg();
            }
            case BR_ZERO -> {
                if (toBool(this.stack.pop()) == false) {
                    return instr.intArg();
                }
            }
            case DUP -> {
                final double right = this.stack.pop();
                this.stack.push(right);
                this.stack.push(right);
            }
            case NOT -> this.stack.push(toDouble(toBool(this.stack.pop()) == false));
            case INVOKE -> this.stack.push(invoke(instr.strArg(), this.stack.pop()));
            case CALL -> {
                this.yield = true;
                final double argCount = this.stack.pop();
                this.stack.insert(this.stack.tail - (int)argCount, this.pc + 1);
                this.stackFrames.push(this.stack.tail - (int)argCount);
                return instr.intArg();
            }
            case RET -> {
                this.yield = true;
                this.stack.tail = this.stackFrames.pop();
                return (int) this.stack.pop();
            }
            case RET_VAL -> {
                this.yield = true;
                final double retVal = this.stack.pop(); // return value
                this.stack.tail = this.stackFrames.pop();
                final int retPC = (int) this.stack.pop();
                this.stack.push(retVal);
                return retPC;
            }
            case LOG -> this.runtime.log(instr.strArg(), this.stack.pop());
            case SWAP -> {
                final double right = this.stack.pop();
                final double left = this.stack.pop();
                this.stack.push(right);
                this.stack.push(left);
            }
        }

        return -1;
    }

    private double invoke(String strArg, double arg) {
        return switch (strArg) {
            case "abs" -> Math.abs(arg);
            case "tan" -> Math.tan(Math.toRadians(arg));
            case "sin" -> Math.sin(Math.toRadians(arg));
            case "cos" -> Math.cos(Math.toRadians(arg));
            case "atan" -> Math.toDegrees(Math.atan(arg));
            case "asin" -> Math.toDegrees(Math.asin(arg));
            case "acos" -> Math.toDegrees(Math.acos(arg));
            case "sqrt" -> Math.sqrt(arg);
            case "trunc" -> Math.floor(arg);
            default -> throw new IllegalArgumentException("Unknown built-in function: '" + strArg + "'");
        };
    }

    private double loadRegister(String strArg) {
        return switch (strArg) {
            case "AIM" -> this.runtime.readAim();
            case "RADAR" -> this.runtime.readRadar();
            case "SPEEDX" -> this.runtime.readSpeedX();
            case "SPEEDY" -> this.runtime.readSpeedY();
            case "X" -> this.runtime.readX();
            case "Y" -> this.runtime.readY();
            case "DAMAGE" -> this.runtime.readDamage();
            case "SHOT" -> this.runtime.readShot();
            case "RANDOM" -> this.runtime.getRandom();
            default -> throw new IllegalArgumentException("Unknown register: '" + strArg + "'");
        };
    }

    private void storeRegister(String strArg, double d) {
        switch (strArg) {
            case "AIM" -> this.runtime.writeAim(d);
            case "RADAR" -> this.runtime.writeRadar(d);
            case "SPEEDX" -> this.runtime.writeSpeedX(d);
            case "SPEEDY" -> this.runtime.writeSpeedY(d);
            case "SHOT" -> this.runtime.writeShot(d);
            default -> throw new IllegalArgumentException("Unknown register: '" + strArg + "'");
        }
    }

    private static boolean toBool(double d) {
        return d != 0.0;
    }

    private static double toDouble(boolean b) {
        return b ? 1.0 : 0.0;
    }

    private static class Stack {
        final double[] array = new double[64];
        int tail;

        void push(double d) throws StackException {
            if (this.tail >= array.length) {
                throw new StackException("Stack overflow");
            }
            this.array[this.tail] = d;
            this.tail++;
        }

        double pop() throws StackException {
            if (this.tail <= 0) {
                throw new StackException("Stack underflow");
            }
            this.tail--;
            return this.array[this.tail];
        }

        void insert(int index, double value) {
            Objects.checkIndex(index, this.tail + 1);
            if (index < this.tail) {
                System.arraycopy(this.array, index, this.array, index + 1, this.tail - index);
            }
            this.array[index] = value;
            this.tail++;
        }

        double get(int i) {
            Objects.checkIndex(i, this.tail);
            return this.array[i];
        }

        void set(int i, double d) {
            Objects.checkIndex(i, this.tail);
            this.array[i] = d;
        }
    }

    public static class StackException extends Exception {
        private int pc;
        private Instruction instruction;

        private StackException(String message) {
            super(message);
        }

        public int pc() {
            return this.pc;
        }

        public Instruction instruction() {
            return this.instruction;
        }
    }
}
