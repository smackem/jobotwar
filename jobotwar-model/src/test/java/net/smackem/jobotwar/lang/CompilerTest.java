package net.smackem.jobotwar.lang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);
        runComplete(interpreter);

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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        System.out.println(program.toString());

        runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(100);
    }

    @Test
    public void testUnless() {
        final String source = "" +
                "def i\n" +
                "1 -> SHOT unless i = 1\n" +
                "2 -> AIM unless i = 0\n";

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

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

        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

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
        final Program program = compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

        assertThat(env.loggedCategories).isEqualTo(Arrays.asList("SHOT", "SPEEDX", "SPEEDY", "a", "b", "a", "AIM"));
        assertThat(env.loggedValues).isEqualTo(Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0));
    }

    private Program compile(String source) {
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source);
        return result.program();
    }

    private void runComplete(Interpreter interpreter) {
        try {
            //noinspection StatementWithEmptyBody
            while (interpreter.runNext()) {
                // proceed until program has finished
            }
        } catch (Interpreter.StackException e) {
            assertThat(true).isFalse();
        }
    }

    private static class TestEnvironment implements RuntimeEnvironment {
        private double aim;
        private double radar;
        private double speedX;
        private double speedY;
        private double x;
        private double y;
        private double shot;
        private double damage;
        private final Collection<Double> loggedValues = new ArrayList<>();
        private final Collection<String> loggedCategories = new ArrayList<>();

        @Override
        public double readAim() {
            return this.aim;
        }

        @Override
        public void writeAim(double value) {
            this.aim = value;
        }

        @Override
        public double readRadar() {
            return this.radar;
        }

        @Override
        public void writeRadar(double value) {
            this.radar = value;
        }

        @Override
        public double readSpeedX() {
            return this.speedX;
        }

        @Override
        public void writeSpeedX(double value) {
            this.speedX = value;
        }

        @Override
        public double readSpeedY() {
            return this.speedY;
        }

        @Override
        public void writeSpeedY(double value) {
            this.speedY = value;
        }

        @Override
        public double readX() {
            return this.x;
        }

        @Override
        public double readY() {
            return this.y;
        }

        @Override
        public double readDamage() {
            return this.damage;
        }

        @Override
        public double readShot() {
            return this.shot;
        }

        @Override
        public void writeShot(double value) {
            this.shot = value;
        }

        @Override
        public double getRandom() {
            return 42;
        }

        @Override
        public void log(String category, double value) {
            this.loggedCategories.add(category);
            this.loggedValues.add(value);
        }

        void setX(double value) {
            this.x = value;
        }

        void setY(double value) {
            this.y = value;
        }

        void setDamage(double value) {
            this.damage = value;
        }

        Collection<Double> loggedValues() {
            return this.loggedValues;
        }

        Collection<String> loggedCategories() {
            return this.loggedCategories;
        }
    }
}