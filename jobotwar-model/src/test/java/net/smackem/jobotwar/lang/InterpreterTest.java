package net.smackem.jobotwar.lang;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InterpreterTest {

    @Test
    public void testCallWithLocals() throws Program.ParseException {
        final String asm = """
                LD_F64      14.0
                LD_F64      0
                CALL        4
                BR          13
                LABEL       4
                LD_F64      0.0
                LD_F64      5
                LD_GLB      0
                ADD
                ST_LOC      0
                LD_LOC      0
                ST_REG      SHOT
                RET
                LABEL       13
                """;
        final Program program = Program.parse(asm);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(19.0);
    }

    @Test
    public void testCallWithParams() throws Program.ParseException {
        final String asm = """
                LD_F64      14.0
                LD_F64      5
                LD_F64      1
                CALL        5
                BR          14
                LABEL       5
                LD_F64      0.0
                LD_LOC      0
                LD_GLB      0
                ADD
                ST_LOC      1
                LD_LOC      1
                ST_REG      SHOT
                RET
                LABEL       14
                """;
        final Program program = Program.parse(asm);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(19.0);
    }

    @Test
    public void testCallWithReturnValue() throws Program.ParseException {
        // function add() { return 14 + 5; }
        // @shot = add()
        final String asm = """
                LD_F64      0
                CALL        4
                ST_REG      SHOT
                BR          9
                LABEL       4
                LD_F64      14
                LD_F64      5
                ADD
                RET_VAL
                LABEL       9
                """;
        final Program program = Program.parse(asm);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(19.0);
    }

    @Test
    public void testCallWithParamsAndReturnValue() throws Program.ParseException {
        // function add(a, b) { return a + b; }
        // @shot = add(14, 5)
        final String asm = """
                LD_F64      14
                LD_F64      5
                LD_F64      2
                CALL        6
                ST_REG      SHOT
                BR          9
                LABEL       6
                ADD
                RET_VAL
                LABEL       9
                """;
        final Program program = Program.parse(asm);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(19.0);
    }

    static void runComplete(Interpreter interpreter) {
        try {
            //noinspection StatementWithEmptyBody
            while (interpreter.runNext()) {
                // proceed until program has finished
            }
        } catch (Interpreter.StackException e) {
            throw new RuntimeException(e);
        }
    }
}