package net.smackem.jobotwar.lang;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerV1Test {

    @Test
    public void testWriteRegisters() {
        final String source = """
                1 -> AIM
                2 -> RADAR
                3 -> SPEEDX
                4 -> SPEEDY
                5 -> SHOT
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);
        InterpreterTest.runComplete(interpreter);

        assertThat(env.readAim()).isEqualTo(1);
        assertThat(env.readRadar()).isEqualTo(2);
        assertThat(env.readSpeedX()).isEqualTo(3);
        assertThat(env.readSpeedY()).isEqualTo(4);
        assertThat(env.readShot()).isEqualTo(5);
    }

    @Test
    public void testReadRegisters() throws Exception {
        final String source = """
                AIM -> SHOT
                RADAR -> SHOT
                SPEEDX -> SHOT
                SPEEDY -> SHOT
                X -> SHOT
                Y -> SHOT
                DAMAGE -> SHOT
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        env.writeAim(1);
        env.writeRadar(2);
        env.writeSpeedX(3);
        env.writeSpeedY(4);
        env.setX(6);
        env.setY(7);
        env.setDamage(8);

        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(1);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(2);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(3);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(4);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(6);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(7);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(8);
        assertThat(interpreter.runNext()).isFalse();
    }

    @Test
    public void testDef() {
        final String source = """
                def x, y
                1.5 -> x
                2.5 -> y
                x + y -> SHOT
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(4);
    }

    @Test
    public void testLoop() {
        final String source = """
                def i
                Loop:
                   i + 1 -> i
                   goto Loop if i < 100
                i -> SHOT
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        System.out.println(program.toString());

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(100);
    }

    @Test
    public void testUnless() {
        final String source = """
                def i
                1 -> SHOT unless i = 1
                2 -> AIM unless i = 0
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(1);
        assertThat(env.readAim()).isNotEqualTo(2);
    }

    @Test
    public void testComplex() {
        final String source = """
                def x
                def y
                def targetX
                def targetY
                100 -> targetX
                60 -> targetY
                MoveX:
                   x + 1 -> x
                   goto MoveX unless x >= targetX
                MoveY:
                   y + 1 -> y
                   goto MoveY unless y >= targetY
                Scan:
                   RADAR + 5 -> RADAR
                   goto Scan if RADAR < 360
                RADAR -> AIM
                200 + RADAR + AIM + x + y -> SHOT
                targetX + 50 -> targetX
                targetY + 10 -> targetY
                goto MoveX unless targetX > 1000
                x -> SPEEDX
                y -> SPEEDY
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readSpeedX()).isGreaterThanOrEqualTo(1000);
        assertThat(env.readShot()).isEqualTo(env.readSpeedX() + env.readSpeedY() + 200 + env.readRadar() + env.readAim());
    }

    @Test
    public void testArithmetic() {
        final String source = """
                1 + 5 / 2 -> SHOT
                2 * 5 - 1 -> SPEEDX
                1 + 5 * 3 -> AIM
                10 - 5 + 3 -> SPEEDY
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(3.5);
        assertThat(env.readSpeedX()).isEqualTo(9);
        assertThat(env.readAim()).isEqualTo(16);
        assertThat(env.readSpeedY()).isEqualTo(8);
    }

    @Test
    public void testMultiAssignment() {
        final String source = """
                def x
                42 -> SHOT -> AIM -> RADAR -> x
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(42);
        assertThat(env.readAim()).isEqualTo(42);
        assertThat(env.readRadar()).isEqualTo(42);
    }

    @Test
    public void testMathOperators() throws Interpreter.StackException {
        final String source = """
                abs(-10) -> SHOT
                abs(10) -> SHOT
                sin(90) -> SHOT
                cos(180) -> SHOT
                atan(tan(90)) -> SHOT
                acos(cos(90)) -> SHOT
                asin(sin(90)) -> SHOT
                not(0) -> SHOT
                not(100) -> SHOT
                sqrt(9) -> SHOT
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(10);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(10);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(1);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(-1);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(90);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(90);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(90);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(1);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(0);
        assertThat(interpreter.runNext()).isTrue();
        assertThat(env.readShot()).isEqualTo(3);
    }

    @Test
    public void testParenExpressions() {
        final String source = """
                abs(100 * (10 - 12)) -> SHOT
                50 / (40 - 3 * abs(-10)) -> AIM
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(200);
        assertThat(env.readAim()).isEqualTo(5);
    }

    @Test
    public void testSubs() {
        final String source = """
                def x
                abs(-100 + 200) -> SHOT
                gosub inc
                x -> AIM
                goto end
                inc:
                x + 1 -> x
                goto inc unless x >= 10
                endsub
                end:
                """;

        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(100);
        assertThat(env.readAim()).isEqualTo(10);
    }

    @Test
    public void testLog() {
        final String source = """
                def a, b
                1 -> SHOT -> OUT
                2 -> SPEEDX -> OUT
                3 -> SPEEDY -> OUT
                4 -> a -> OUT
                5 -> b -> OUT
                6 -> a
                a -> OUT
                7 -> AIM
                AIM -> OUT
                """;
        final Program program = compile(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.loggedCategories()).isEqualTo(Arrays.asList("SHOT", "SPEEDX", "SPEEDY", "a", "b", "a", "AIM"));
        assertThat(env.loggedValues()).isEqualTo(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0));
    }

    private Program compile(String source) {
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source, Compiler.Language.V1);
        assertThat(result.hasErrors()).isFalse();
        return result.program();
    }
}