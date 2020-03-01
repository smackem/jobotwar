package net.smackem.jobotwar.lang;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InterpreterTest {

    @Test
    public void testCallWithLocals() throws Program.ParseException {
        final String asm = "" +
                "LD_F64      14.0\n" +
                "LD_F64      0\n" +     // 0 function arguments
                "CALL        4\n" +
                "BR          13\n" +
                "LABEL       4\n" +
                "LD_F64      0.0\n" +
                "LD_F64      5\n" +
                "LD_GLB      0\n" +
                "ADD\n" +
                "ST_LOC      0\n" +
                "LD_LOC      0\n" +
                "ST_REG      SHOT\n" +
                "RET\n" +
                "LABEL       13";
        final Program program = Program.parse(asm);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(19.0);
    }

    @Test
    public void testCallWithParams() throws Program.ParseException {
        final String asm = "" +
                "LD_F64      14.0\n" +  // glb_0
                "LD_F64      5\n" +     // param_0
                "LD_F64      1\n" +     // no of params
                "CALL        5\n" +
                "BR          14\n" +
                "LABEL       5\n" +
                "LD_F64      0.0\n" +   // loc_1
                "LD_LOC      0\n" +     // loc_1 = glb_0 + param_0
                "LD_GLB      0\n" +
                "ADD\n" +
                "ST_LOC      1\n" +
                "LD_LOC      1\n" +
                "ST_REG      SHOT\n" +
                "RET\n" +
                "LABEL       14";
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
        final String asm = "" +
                "LD_F64      0\n" +     // no of param
                "CALL        4\n" +
                "ST_REG      SHOT\n" +
                "BR          9\n" +
                "LABEL       4\n" +
                "LD_F64      14\n" +
                "LD_F64      5\n" +
                "ADD\n" +
                "RET_VAL\n" +
                "LABEL       9\n";
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
        final String asm = "" +
                "LD_F64      14\n" +
                "LD_F64      5\n" +
                "LD_F64      2\n" +
                "CALL        6\n" +
                "ST_REG      SHOT\n" +
                "BR          9\n" +
                "LABEL       6\n" +
                "ADD\n" +
                "RET_VAL\n" +
                "LABEL       9\n";
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