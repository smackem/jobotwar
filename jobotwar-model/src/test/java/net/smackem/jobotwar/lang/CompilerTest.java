package net.smackem.jobotwar.lang;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerTest {

    @Test
    public void testWriteRegisters() {
        final String source = """
                1 -> AIM
                2 -> RADAR
                3 -> SPEEDX
                4 -> SPEEDY
                5 -> SHOT
                """;

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
        final String source = """
                AIM -> SHOT
                RADAR -> SHOT
                SPEEDX -> SHOT
                SPEEDY -> SHOT
                X -> SHOT
                Y -> SHOT
                DAMAGE -> SHOT
                """;

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
        final String source = """
                def i
                1 -> SHOT unless i = 1
                2 -> AIM unless i = 0
                """;

        final Program program = compileV1(source);
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

        final Program program = compileV1(source);
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
        final String source = """
                def x
                42 -> SHOT -> AIM -> RADAR -> x
                """;

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
        final String source = """
                abs(100 * (10 - 12)) -> SHOT
                50 / (40 - 3 * abs(-10)) -> AIM
                """;

        final Program program = compileV1(source);
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

        final Program program = compileV1(source);
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
        final Program program = compileV1(source);
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.loggedCategories()).isEqualTo(Arrays.asList("SHOT", "SPEEDX", "SPEEDY", "a", "b", "a", "AIM"));
        assertThat(env.loggedValues()).isEqualTo(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0));
    }

    @Test
    public void testV2Assignments() {
        final String source = """
                def global = 12
                state main() {
                   def local = 13 + global
                   global = local
                   @radar(global)
                   exit
                }
                """;
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(25.0);
    }

    @Test
    public void testV2Yield() {
        final String source = """
                state main() {
                   yield second()
                }
                state second() {
                   @radar(42)
                   exit
                }
                """;
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(42.0);
    }

    @Test
    public void testV2StateParams() {
        final String source = """
                state main() {
                   yield second(42, 10, 5)
                }
                state second(number1, number2, number3) {
                   @radar(number1 - number2 - number3)
                   exit
                }
                """;
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(27);
    }


    @Test
    public void testV2YieldMultiple() {
        final String source = """
                state main() { yield second() }
                state second() { yield third() }
                state third() { yield fourth() }
                state fourth() { yield final(42) }
                state final(result) {
                   @radar(result)
                   exit
                }
                """;
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(42.0);
    }

    @Test
    public void testV2Registers() {
        final String source = """
                state main() {
                   @speed(101, 102)
                   @fire(90, 1000)
                   @radar(180)
                   exit
                }
                """;
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
        final String source = """
                state main() {
                   @speedX(101 - 100 + 10)
                   @speedY(20 - 5*2)
                   @radar(10/2 + 15)
                   exit
                }
                """;
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
        final String source = """
                state main() {
                   def n = 5
                   def i = 10
                   while n < 5 {
                       i = i + 1
                   }
                   @radar(i)
                   exit
                }
                """;
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(10);
    }

    @Test
    public void testV2WhileNested() {
        final String source = """
                state main() {
                   def x = 0
                   def i = 0
                   while i < 5 {
                       def j = 0
                       while j < 5 {
                           x = x + i * j
                           j = j + 1
                       }
                       i = i + 1
                   }
                   @radar(x)
                   exit
                }
                """;
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(100);
    }

    @Test
    public void testV2If() {
        final String source = """
                state main() {
                   def x = 2
                   if x == 1 { @radar(1) }
                   if x == 2 { @radar(2) }
                   exit
                }
                """;
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(2);
    }

    @Test
    public void testV2IfElse() {
        final String source = """
                state main() {
                   def x = 2
                   if x == 1 { @radar(1) }
                   else { @radar(2) }
                   exit
                }
                """;
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(2);
    }

    @Test
    public void testV2IfElseIf() {
        final String source = """
                state main() {
                   def x = 3
                   if x == 1 { @radar(1) }
                   else if x == 2 { @radar(2) }
                   else if x == 3 { @radar(3) }
                   else { @radar(10) }
                   exit
                }
                """;
        final Program program = compileV2(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(3);
    }

    @Test
    public void testV2Random0() {
        final String source = """
                state main() {
                   @radar(@random())
                   exit
                }
                """;
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
        final String source = """
                state main() {
                   @radar(@random(10))
                   exit
                }
                """;
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
        final String source = """
                state main() {
                   @radar(@random(100, 120))
                   exit
                }
                """;
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