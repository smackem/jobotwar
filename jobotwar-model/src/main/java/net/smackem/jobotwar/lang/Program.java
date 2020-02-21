package net.smackem.jobotwar.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A compiled program.
 */
public final class Program {
    private final List<Instruction> instructions;
    private static final Pattern INSTRUCTION_PATTERN = Pattern.compile(
            "^(?<OPCODE>[A-Z0-9_]+)(\\s+(?<ARG>.+))?$");

    /**
     * Initializes a new instance of {@link Program}.
     * @param instructions The emitted instructions.
     */
    Program(List<Instruction> instructions) {
        this.instructions = Objects.requireNonNull(instructions);
    }

    /**
     * @return The emitted instructions that make up the program.
     */
    Collection<Instruction> instructions() {
        return this.instructions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Instruction instr : this.instructions) {
            final String arg = formatArg(instr);
            if (arg != null) {
                sb.append(String.format("%-12s%s\n", instr.opCode(), arg));
            } else {
                sb.append(instr.opCode());
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public static Program parse(String source) throws ParseException {
        Objects.requireNonNull(source);
        try (final StringReader sr = new StringReader(source);
             final BufferedReader reader = new BufferedReader(sr)) {

            final List<Instruction> instructions = new ArrayList<>();
            int lineNo = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    instructions.add(parseInstruction(line));
                } catch (IllegalArgumentException ex) {
                    throw new ParseException(String.format("line %d: %s", lineNo, ex.getMessage()), ex);
                }
                lineNo++;
            }
            return new Program(instructions);

        } catch (IOException e) {
            throw new RuntimeException(); // should never happen on an in-memory reader
        }
    }

    private static Instruction parseInstruction(String line) {
        final Matcher matcher = INSTRUCTION_PATTERN.matcher(line);
        if (matcher.find() == false) {
            return null;
        }
        final OpCode opCode = OpCode.valueOf(matcher.group("OPCODE"));
        final String arg = matcher.group("ARG");
        switch (opCode) {
            case LD_F64:
                return new Instruction(opCode, Double.parseDouble(arg));
            case LD_LOC:
            case ST_LOC:
            case LD_GLB:
            case ST_GLB:
            case LABEL:
            case BR:
            case BR_ZERO:
            case CALL:
                return new Instruction(opCode, Integer.parseInt(arg));
            case LD_REG:
            case ST_REG:
            case INVOKE:
            case LOG:
                return new Instruction(opCode, arg);
            default:
                return new Instruction(opCode);
        }
    }

    private static String formatArg(Instruction instr) {
        switch (instr.opCode()) {
            case LD_F64:
                return String.valueOf(instr.f64Arg());
            case LD_LOC:
            case ST_LOC:
            case LD_GLB:
            case ST_GLB:
            case LABEL:
            case BR:
            case BR_ZERO:
            case CALL:
                return String.valueOf(instr.intArg());
            case LD_REG:
            case ST_REG:
            case INVOKE:
            case LOG:
                return instr.strArg();
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Program program = (Program) o;

        return instructions.equals(program.instructions);
    }

    @Override
    public int hashCode() {
        return instructions.hashCode();
    }

    public static class ParseException extends Exception {
        private ParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
