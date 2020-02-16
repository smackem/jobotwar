package net.smackem.jobotwar.lang;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProgramTest {

    @Test
    public void testEquals() {
        final Program program1 = new Program(Arrays.asList(
                new Instruction(OpCode.LD_F64, 1.0),
                new Instruction(OpCode.LD_REG, "SHOT"),
                new Instruction(OpCode.BR, 123)
        ));
        final Program program2 = new Program(Arrays.asList(
                new Instruction(OpCode.LD_F64, 1.0),
                new Instruction(OpCode.LD_REG, "SHOT"),
                new Instruction(OpCode.BR, 123)
        ));
        assertThat(program1).isEqualTo(program2);
        assertThat(program1.hashCode()).isEqualTo(program2.hashCode());
        final Program program3 = new Program(Arrays.asList(
                new Instruction(OpCode.LD_F64, 1.0),
                new Instruction(OpCode.LD_REG, "SPEEDX")
        ));
        assertThat(program1).isNotEqualTo(program3);
        assertThat(program2).isNotEqualTo(program3);
        final Program empty1 = new Program(Collections.emptyList());
        final Program empty2 = new Program(Collections.emptyList());
        assertThat(empty1).isEqualTo(empty2);
        assertThat(empty1.hashCode()).isEqualTo(empty2.hashCode());
    }

    @Test
    public void testToString() {
        final Program program = new Program(Arrays.asList(
                new Instruction(OpCode.LABEL, 0),
                new Instruction(OpCode.LD_F64, 1.0),
                new Instruction(OpCode.LD_F64, 2.0),
                new Instruction(OpCode.LD_REG, "AIM"),
                new Instruction(OpCode.ADD),
                new Instruction(OpCode.BR, 0)
        ));
        final String s = stripWhitespace(program.toString());
        assertThat(s).isEqualTo("" +
                "LABEL 0\n" +
                "LD_F64 1.0\n" +
                "LD_F64 2.0\n" +
                "LD_REG AIM\n" +
                "ADD\n" +
                "BR 0\n");
    }

    @Test
    public void parse() throws Program.ParseException {
        final String source = "" +
                "LABEL 0\n" +
                "LABEL 1\n" +
                "LD_F64 125.5\n" +
                "LD_REG RADAR\n" +
                "DUP\n" +
                "SUB\n" +
                "ADD\n" +
                "MUL\n" +
                "BR_ZERO 0\n" +
                "INVOKE abs\n";
        final Program program = Program.parse(source);
        assertThat(program).isEqualTo(new Program(Arrays.asList(
                new Instruction(OpCode.LABEL, 0),
                new Instruction(OpCode.LABEL, 1),
                new Instruction(OpCode.LD_F64, 125.5),
                new Instruction(OpCode.LD_REG, "RADAR"),
                new Instruction(OpCode.DUP),
                new Instruction(OpCode.SUB),
                new Instruction(OpCode.ADD),
                new Instruction(OpCode.MUL),
                new Instruction(OpCode.BR_ZERO, 0),
                new Instruction(OpCode.INVOKE, "abs")
        )));
    }

    @Test
    public void parseEmpty() throws Program.ParseException {
        assertThat(Program.parse("")).isEqualTo(new Program(Collections.emptyList()));
    }

    @Test
    public void parseError() {
        assertThatThrownBy(() -> Program.parse("LD_F64 x"))
                .isInstanceOf(Program.ParseException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("line 1");
        assertThatThrownBy(() -> Program.parse("GURKE"))
                .isInstanceOf(Program.ParseException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("line 1");
        assertThatThrownBy(() -> Program.parse("BR 0\nLABEL 1\nWTF 123"))
                .isInstanceOf(Program.ParseException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("line 3");
    }

    private static String stripWhitespace(String s) {
        return s.replaceAll(" +", " ")
                .replaceAll("[\\r\\n]+", "\n");
    }
}
