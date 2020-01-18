package net.smackem.jobotwar.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class Interpreter {
    private static final Logger log = LoggerFactory.getLogger(Interpreter.class);
    private final Program program;
    private final List<Instruction> code;
    private final RuntimeEnvironment runtime;
    private final Stack stack = new Stack();
    private boolean registerStored;
    private int pc;

    public Interpreter(Program program, RuntimeEnvironment runtime) {
        this.program = Objects.requireNonNull(program);
        this.code = new ArrayList<>(program.instructions());
        this.runtime = Objects.requireNonNull(runtime);
    }

    public Program program() {
        return this.program;
    }

    public RuntimeEnvironment runtime() {
        return this.runtime;
    }

    public boolean runNext() throws StackException {
        final int codeSize = this.code.size();
        while (this.pc < codeSize) {
            final int target = executeInstruction(this.code.get(this.pc));
            if (target >= 0) {
                this.pc = target;
                return true;
            }
            this.pc++;
            if (target < -1) {
                return true;
            }
        }
        return false;
    }

    private int executeInstruction(Instruction instr) throws StackException {
        double right;
        switch (instr.opCode()) {
            case LD_F64:
                this.stack.push(instr.f64Arg());
                break;
            case LD_REG:
                this.stack.push(loadRegister(instr.strArg()));
                break;
            case LD_LOC:
                this.stack.push(this.stack.get(instr.intArg()));
                break;
            case ST_LOC:
                right = this.stack.pop();
                this.stack.set(instr.intArg(), right);
                break;
            case ST_REG:
                right = this.stack.pop();
                storeRegister(instr.strArg(), right);
                this.registerStored = true;
                break;
            case ADD:
                this.stack.push(this.stack.pop() + this.stack.pop());
                break;
            case SUB:
                right = this.stack.pop();
                this.stack.push(this.stack.pop() - right);
                break;
            case MUL:
                this.stack.push(this.stack.pop() * this.stack.pop());
                break;
            case DIV:
                right = this.stack.pop();
                this.stack.push(this.stack.pop() / right);
                break;
            case MOD:
                right = this.stack.pop();
                this.stack.push(this.stack.pop() % right);
                break;
            case OR:
                right = this.stack.pop();
                this.stack.push(toDouble(toBool(this.stack.pop()) || toBool(right)));
                break;
            case AND:
                right = this.stack.pop();
                this.stack.push(toDouble(toBool(this.stack.pop()) && toBool(right)));
                break;
            case EQ:
                this.stack.push(toDouble(this.stack.pop() == this.stack.pop()));
                break;
            case NEQ:
                this.stack.push(toDouble(this.stack.pop() != this.stack.pop()));
                break;
            case GT:
                right = this.stack.pop();
                this.stack.push(toDouble(this.stack.pop() > right));
                break;
            case GE:
                right = this.stack.pop();
                this.stack.push(toDouble(this.stack.pop() >= right));
                break;
            case LT:
                right = this.stack.pop();
                this.stack.push(toDouble(this.stack.pop() < right));
                break;
            case LE:
                right = this.stack.pop();
                this.stack.push(toDouble(this.stack.pop() <= right));
                break;
            case LABEL:
                if (this.registerStored) {
                    this.registerStored = false;
                    return -2;
                }
                break;
            case BR:
                return instr.intArg();
            case BR_ZERO:
                if (toBool(this.stack.pop()) == false) {
                    return instr.intArg();
                }
                break;
            case DUP:
                right = this.stack.pop();
                this.stack.push(right);
                this.stack.push(right);
                break;
            case NOT:
                this.stack.push(toDouble(toBool(this.stack.pop()) == false));
                break;
            case INVOKE:
                this.stack.push(invoke(instr.strArg(), this.stack.pop()));
                break;
            case CALL:
                this.stack.push(this.pc + 1);
                return instr.intArg();
            case RET:
                return (int)this.stack.pop();
            case LOG:
                this.runtime.log(instr.strArg(), this.stack.pop());
                break;
        }

        return -1;
    }

    private double invoke(String strArg, double arg) {
        switch (strArg) {
            case "abs":     return Math.abs(arg);
            case "tan":     return Math.tan(Math.toRadians(arg));
            case "sin":     return Math.sin(Math.toRadians(arg));
            case "cos":     return Math.cos(Math.toRadians(arg));
            case "atan":    return Math.toDegrees(Math.atan(arg));
            case "asin":    return Math.toDegrees(Math.asin(arg));
            case "acos":    return Math.toDegrees(Math.acos(arg));
            case "sqrt":    return Math.sqrt(arg);
            default:
                throw new IllegalArgumentException("Unknown built-in function: '" + strArg + "'");
        }
    }

    private double loadRegister(String strArg) {
        switch (strArg) {
            case "AIM":     return this.runtime.readAim();
            case "RADAR":   return this.runtime.readRadar();
            case "SPEEDX":  return this.runtime.readSpeedX();
            case "SPEEDY":  return this.runtime.readSpeedY();
            case "X":       return this.runtime.readX();
            case "Y":       return this.runtime.readY();
            case "DAMAGE":  return this.runtime.readDamage();
            case "SHOT":    return this.runtime.readShot();
            case "RANDOM":  return this.runtime.getRandom();
            default:
                throw new IllegalArgumentException("Unknown register: '" + strArg + "'");
        }
    }

    private void storeRegister(String strArg, double d) {
        switch (strArg) {
            case "AIM":     this.runtime.writeAim(d); break;
            case "RADAR":   this.runtime.writeRadar(d); break;
            case "SPEEDX":  this.runtime.writeSpeedX(d); break;
            case "SPEEDY":  this.runtime.writeSpeedY(d); break;
            case "SHOT":    this.runtime.writeShot(d); break;
            default:
                throw new IllegalArgumentException("Unknown register: '" + strArg + "'");
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

        double get(int i) {
            return this.array[i];
        }

        void set(int i, double d) {
            this.array[i] = d;
        }
    }

    public static class StackException extends Exception {
        private StackException(String message) {
            super(message);
        }
    }
}
