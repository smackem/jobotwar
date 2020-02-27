package net.smackem.jobotwar.lang;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InterpreterTest {

    @Test
    public void testLocals() throws Program.ParseException {
        final String asm = "" +
                "LD_F64      14.0\n" +
                "CALL        3\n" +
                "BR          12\n" +
                "LABEL       3\n" +
                "LD_F64      0.0\n" +
                "LD_F64      5\n" +
                "LD_GLB      0\n" +
                "ADD\n" +
                "ST_LOC      0\n" +
                "LD_LOC      0\n" +
                "ST_REG      SHOT\n" +
                "RET\n" +
                "LABEL       12";
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