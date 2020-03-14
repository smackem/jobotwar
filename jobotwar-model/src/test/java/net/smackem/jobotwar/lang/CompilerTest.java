package net.smackem.jobotwar.lang;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerTest {

    @Test
    public void testWriteRegisters() {
        final String source = "" +
                "1 -> AIM\n" +
                "2 -> RADAR\n" +
                "3 -> SPEEDX\n" +
                "4 -> SPEEDY\n" +
                "5 -> SHOT\n";

        final Program program = compileV1(source);
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
        final String source = "" +
                "AIM -> SHOT\n" +
                "RADAR -> SHOT\n" +
                "SPEEDX -> SHOT\n" +
                "SPEEDY -> SHOT\n" +
                "X -> SHOT\n" +
                "Y -> SHOT\n" +
                "DAMAGE -> SHOT\n";

        final Program program = compileV1(source);
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
        final String source = "" +
                "def x, y\n" +
                "1.5 -> x\n" +
                "2.5 -> y\n" +
                "x + y -> SHOT\n";

        final Program program = compileV1(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(4);
    }

    @Test
    public void testLoop() {
        final String source = "" +
                "def i\n" +
                "Loop:\n" +
                "   i + 1 -> i\n" +
                "   goto Loop if i < 100\n" +
                "i -> SHOT\n";

        final Program program = compileV1(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        System.out.println(program.toString());

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(100);
    }

    @Test
    public void testUnless() {
        final String source = "" +
                "def i\n" +
                "1 -> SHOT unless i = 1\n" +
                "2 -> AIM unless i = 0\n";

        final Program program = compileV1(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(1);
        assertThat(env.readAim()).isNotEqualTo(2);
    }

    @Test
    public void testComplex() {
        final String source = "" +
                "def x\n" +
                "def y\n" +
                "def targetX\n" +
                "def targetY\n" +
                "100 -> targetX\n" +
                "60 -> targetY\n" +
                "MoveX:\n" +
                "   x + 1 -> x\n" +
                "   goto MoveX unless x >= targetX\n" +
                "MoveY:\n" +
                "   y + 1 -> y\n" +
                "   goto MoveY unless y >= targetY\n" +
                "Scan:\n" +
                "   RADAR + 5 -> RADAR\n" +
                "   goto Scan if RADAR < 360\n" +
                "RADAR -> AIM\n" +
                "200 + RADAR + AIM + x + y -> SHOT\n" +
                "targetX + 50 -> targetX\n" +
                "targetY + 10 -> targetY\n" +
                "goto MoveX unless targetX > 1000\n" +
                "x -> SPEEDX\n" +
                "y -> SPEEDY\n";

        final Program program = compileV1(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readSpeedX()).isGreaterThanOrEqualTo(1000);
        assertThat(env.readShot()).isEqualTo(env.readSpeedX() + env.readSpeedY() + 200 + env.readRadar() + env.readAim());
    }

    @Test
    public void testArithmetic() {
        final String source = "" +
                "1 + 5 / 2 -> SHOT\n" +
                "2 * 5 - 1 -> SPEEDX\n" +
                "1 + 5 * 3 -> AIM\n" +
                "10 - 5 + 3 -> SPEEDY\n";

        final Program program = compileV1(source);
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
        final String source = "" +
                "def x\n" +
                "42 -> SHOT -> AIM -> RADAR -> x \n";

        final Program program = compileV1(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(42);
        assertThat(env.readAim()).isEqualTo(42);
        assertThat(env.readRadar()).isEqualTo(42);
    }

    @Test
    public void testMathOperators() throws Interpreter.StackException {
        final String source = "" +
                "abs(-10) -> SHOT\n" +
                "abs(10) -> SHOT\n" +
                "sin(90) -> SHOT\n" +
                "cos(180) -> SHOT\n" +
                "atan(tan(90)) -> SHOT\n" +
                "acos(cos(90)) -> SHOT\n" +
                "asin(sin(90)) -> SHOT\n" +
                "not(0) -> SHOT\n" +
                "not(100) -> SHOT\n" +
                "sqrt(9) -> SHOT\n";

        final Program program = compileV1(source);
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
        final String source = "" +
                "abs(100 * (10 - 12)) -> SHOT\n" +
                "50 / (40 - 3 * abs(-10)) -> AIM\n";

        final Program program = compileV1(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(200);
        assertThat(env.readAim()).isEqualTo(5);
    }

    @Test
    public void testSubs() {
        final String source = "" +
                "def x\n" +
                "abs(-100 + 200) -> SHOT\n" +
                "gosub inc\n" +
                "x -> AIM\n" +
                "goto end\n" +
                "inc:\n" +
                "x + 1 -> x\n" +
                "goto inc unless x >= 10\n" +
                "endsub\n" +
                "end:";

        final Program program = compileV1(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(100);
        assertThat(env.readAim()).isEqualTo(10);
    }

    @Test
    public void testLog() {
        final String source = "" +
                "def a, b\n" +
                "1 -> SHOT -> OUT\n" +
                "2 -> SPEEDX -> OUT\n" +
                "3 -> SPEEDY -> OUT\n" +
                "4 -> a -> OUT\n" +
                "5 -> b -> OUT\n" +
                "6 -> a\n" +
                "a -> OUT\n" +
                "7 -> AIM\n" +
                "AIM -> OUT\n";
        final Program program = compileV1(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.loggedCategories()).isEqualTo(Arrays.asList("SHOT", "SPEEDX", "SPEEDY", "a", "b", "a", "AIM"));
        assertThat(env.loggedValues()).isEqualTo(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0));
    }

    @Test
    public void testV2Assignments() {
        final String source = "" +
                "def global = 12" +
                "state main() {\n" +
                "   def local = 13 + global\n" +
                "   global = local\n" +
                "   @radar(global)\n" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(25.0);
    }

    @Test
    public void testV2Yield() {
        final String source = "" +
                "state main() {\n" +
                "   yield second()\n" +
                "}\n" +
                "state second() {\n" +
                "   @radar(42)" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(42.0);
    }

    @Test
    public void testV2StateParams() {
        final String source = "" +
                "state main() {\n" +
                "   yield second(42, 10, 5)\n" +
                "}\n" +
                "state second(number1, number2, number3) {\n" +
                "   @radar(number1 - number2 - number3)" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(27);
    }


    @Test
    public void testV2YieldMultiple() {
        final String source = "" +
                "state main() { yield second() }\n" +
                "state second() { yield third() }\n" +
                "state third() { yield fourth() }\n" +
                "state fourth() { yield final(42) }\n" +
                "state final(result) {\n" +
                "   @radar(result)" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(42.0);
    }

    @Test
    public void testV2Registers() {
        final String source = "" +
                "state main() {\n" +
                "   @speed(101, 102)\n" +
                "   @fire(90, 1000)" +
                "   @radar(180)" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readSpeedX()).isEqualTo(101);
        assertThat(env.readSpeedY()).isEqualTo(102);
        assertThat(env.readAim()).isEqualTo(90);
        assertThat(env.readShot()).isEqualTo(1000);
        assertThat(env.readRadar()).isEqualTo(180);
    }

    @Test
    public void testV2Arithmetic() {
        final String source = "" +
                "state main() {\n" +
                "   @speedX(101 - 100 + 10)\n" +
                "   @speedY(20 - 5*2)\n" +
                "   @radar(10/2 + 15)" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readSpeedX()).isEqualTo(11);
        assertThat(env.readSpeedY()).isEqualTo(10);
        assertThat(env.readRadar()).isEqualTo(20);
    }

    @Test
    public void testV2While() {
        final String source = "" +
                "state main() {\n" +
                "   def i = 0\n" +
                "   while i < 10 {\n" +
                "       i = i + 1\n" +
                "   }\n" +
                "   @radar(i)" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(10);
    }

    @Test
    public void testV2WhileNoLoop() {
        final String source = "" +
                "state main() {\n" +
                "   def n = 5\n" +
                "   def i = 10\n" +
                "   while n < 5 {\n" +
                "       i = i + 1\n" +
                "   }\n" +
                "   @radar(i)" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(10);
    }

    @Test
    public void testV2WhileNested() {
        final String source = "" +
                "state main() {\n" +
                "   def x = 0\n" +
                "   def i = 0\n" +
                "   while i < 5 {\n" +
                "       def j = 0\n" +
                "       while j < 5 {\n" +
                "           x = x + i * j\n" +
                "           j = j + 1\n" +
                "       }\n" +
                "       i = i + 1\n" +
                "   }\n" +
                "   @radar(x)" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(100);
    }

    @Test
    public void testV2If() {
        final String source = "" +
                "state main() {\n" +
                "   def x = 2\n" +
                "   if x == 1 { @radar(1) }\n" +
                "   if x == 2 { @radar(2) }\n" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(2);
    }

    @Test
    public void testV2IfElse() {
        final String source = "" +
                "state main() {\n" +
                "   def x = 2\n" +
                "   if x == 1 { @radar(1) }\n" +
                "   else { @radar(2) }\n" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(2);
    }

    @Test
    public void testV2IfElseIf() {
        final String source = "" +
                "state main() {\n" +
                "   def x = 3\n" +
                "   if x == 1 { @radar(1) }\n" +
                "   else if x == 2 { @radar(2) }\n" +
                "   else if x == 3 { @radar(3) }\n" +
                "   else { @radar(10) }\n" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(3);
    }

    @Test
    public void testV2Random0() {
        final String source = "" +
                "state main() {\n" +
                "   @radar(@random())\n" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        for (int i = 0; i < 100; i++) {
            InterpreterTest.runComplete(interpreter);
            assertThat(env.readRadar()).isBetween(0.0, 1.0);
        }
    }

    @Test
    public void testV2Random1() {
        final String source = "" +
                "state main() {\n" +
                "   @radar(@random(10))\n" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        for (int i = 0; i < 100; i++) {
            InterpreterTest.runComplete(interpreter);
            assertThat(env.readRadar()).isBetween(0.0, 10.0);
        }
    }

    @Test
    public void testV2Random2() {
        final String source = "" +
                "state main() {\n" +
                "   @radar(@random(100, 120))\n" +
                "   exit\n" +
                "}\n";
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        for (int i = 0; i < 100; i++) {
            InterpreterTest.runComplete(interpreter);
            assertThat(env.readRadar()).isBetween(100.0, 120.0);
        }
    }

    private Program compileV1(String source) {
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source, Compiler.Language.V1);
        assertThat(result.hasErrors()).isFalse();
        return result.program();
    }

    private Program compileV2(String source) {
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source, Compiler.Language.V2);
        assertThat(result.hasErrors()).isFalse();
        return result.program();
    }
}