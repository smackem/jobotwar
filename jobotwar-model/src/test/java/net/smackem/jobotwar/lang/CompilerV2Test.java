package net.smackem.jobotwar.lang;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerV2Test {
    @Test
    public void testYield() {
        final String source = """
                state main() {
                   yield second()
                }
                state second() {
                   @radar(42)
                   exit
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(42.0);
    }

    @Test
    public void testAssignments() {
        final String source = """
                def global = 12
                state main() {
                   def local = 13 + global
                   global = local
                   @radar(global)
                   exit
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(25.0);
    }

    @Test
    public void testStateParams() {
        final String source = """
                state main() {
                   yield second(42, 10, 5)
                }
                state second(number1, number2, number3) {
                   @radar(number1 - number2 - number3)
                   exit
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(27);
    }


    @Test
    public void testYieldMultiple() {
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
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(42.0);
    }

    @Test
    public void testRegisters() {
        final String source = """
                state main() {
                   @speed(101, 102)
                   @fire(90, 1000)
                   @radar(180)
                   exit
                }
                """;
        final Program program = compile(source);
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
    public void testArithmetic() {
        final String source = """
                state main() {
                   @speedX(101 - 100 + 10)
                   @speedY(20 - 5*2)
                   @radar(10/2 + 15)
                   exit
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readSpeedX()).isEqualTo(11);
        assertThat(env.readSpeedY()).isEqualTo(10);
        assertThat(env.readRadar()).isEqualTo(20);
    }

    @Test
    public void testWhile() {
        final String source = """
                state main() {
                   def i = 0
                   while i < 10 {
                       i = i + 1
                   }
                   @radar(i)   exit
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(10);
    }

    @Test
    public void testWhileNoLoop() {
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
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(10);
    }

    @Test
    public void testWhileNested() {
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
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(100);
    }

    @Test
    public void testIf() {
        final String source = """
                state main() {
                   def x = 2
                   if x == 1 { @radar(1) }
                   if x == 2 { @radar(2) }
                   exit
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(2);
    }

    @Test
    public void testIfElse() {
        final String source = """
                state main() {
                   def x = 2
                   if x == 1 { @radar(1) }
                   else { @radar(2) }
                   exit
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);

        assertThat(env.readRadar()).isEqualTo(2);
    }

    @Test
    public void testIfElseIf() {
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
        final Program program = compile(source);
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
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        for (int i = 0; i < 100; i++) {
            InterpreterTest.runComplete(interpreter);
            assertThat(env.readRadar()).isBetween(0.0, 1.0);
        }
    }

    @Test
    public void testRandom1() {
        final String source = """
                state main() {
                   @radar(@random(10))
                   exit
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        for (int i = 0; i < 100; i++) {
            InterpreterTest.runComplete(interpreter);
            assertThat(env.readRadar()).isBetween(0.0, 10.0);
        }
    }

    @Test
    public void testRandom2() {
        final String source = """
                state main() {
                   @radar(@random(100, 120))
                   exit
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        for (int i = 0; i < 100; i++) {
            InterpreterTest.runComplete(interpreter);
            assertThat(env.readRadar()).isBetween(100.0, 120.0);
        }
    }

    @Test
    public void testFunction() {
        final String source = """
                state main() {
                    @radar(func(1, 2, 3))
                    exit
                }
                def func(a, b, c) {
                    @log(a)
                    @log(b)
                    @log(c)
                    return a + b + c
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);
        assertThat(env.loggedValues()).containsExactly(1.0, 2.0, 3.0);
        assertThat(env.readRadar()).isEqualTo(6.0);
    }

    @Test
    public void testRecursion() {
        final String source = """
                state main() {
                    @radar(recurse(1))
                    exit
                }
                def recurse(n) {
                    if n == 10 {
                        return n
                    }
                    @log(n)
                    return recurse(n + 1)
                }
                """;
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);
        assertThat(env.loggedValues()).containsExactly(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0);
        assertThat(env.readRadar()).isEqualTo(10.0);
    }

    @Test
    public void testBuiltInFunctions() {
        final String source = String.format("""
                state main() {
                    @log(sign(100))
                    @log(sign(-100))
                    @log(sign(0))
                    @log(min(-10, 20))
                    @log(max(-10, 20))
                    @log(hypot(1, 0))
                    @log(hypot(1, 1))
                    @log(atan(%f))
                    @log(atan(%f))
                    exit
                }
                """, Math.tan(Math.toRadians(90)), Math.tan(Math.toRadians(-90)));
        final Program program = compile(source);
        System.out.println(program.toString());
        final TestRuntimeEnvironment env = new TestRuntimeEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        InterpreterTest.runComplete(interpreter);
        assertThat(env.loggedValues()).containsExactly(
                1.0, -1.0, 0.0, -10.0, 20.0, 1.0, Math.sqrt(2), 90.0, -90.0);
    }

    private Program compile(String source) {
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source, Compiler.Language.V2);
        assertThat(result.hasErrors()).isFalse();
        return result.program();
    }
}
