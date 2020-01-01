package net.smackem.jobotwar.lang;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class CompilerTest {

    @Test
    public void testWriteRegisters() {
        final String source = "" +
                "1 => AIM\n" +
                "2 => RADAR\n" +
                "3 => SPEEDX\n" +
                "4 => SPEEDY\n" +
                "5 => SHOT\n";

        final Compiler compiler = new Compiler();
        final Program program = compiler.compile(source);
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
                "AIM => SHOT\n" +
                "RADAR => SHOT\n" +
                "SPEEDX => SHOT\n" +
                "SPEEDY => SHOT\n" +
                "X => SHOT\n" +
                "Y => SHOT\n" +
                "DAMAGE => SHOT\n";

        final Compiler compiler = new Compiler();
        final Program program = compiler.compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        env.writeAim(1);
        env.writeRadar(2);
        env.writeSpeedX(3);
        env.writeSpeedY(4);
        env.setX(6);
        env.setY(7);
        env.setDamage(8);

        assertThat(interpreter.runToNextLabel()).isTrue();
        assertThat(env.readShot()).isEqualTo(1);
        assertThat(interpreter.runToNextLabel()).isTrue();
        assertThat(env.readShot()).isEqualTo(2);
        assertThat(interpreter.runToNextLabel()).isTrue();
        assertThat(env.readShot()).isEqualTo(3);
        assertThat(interpreter.runToNextLabel()).isTrue();
        assertThat(env.readShot()).isEqualTo(4);
        assertThat(interpreter.runToNextLabel()).isTrue();
        assertThat(env.readShot()).isEqualTo(6);
        assertThat(interpreter.runToNextLabel()).isTrue();
        assertThat(env.readShot()).isEqualTo(7);
        assertThat(interpreter.runToNextLabel()).isTrue();
        assertThat(env.readShot()).isEqualTo(8);
    }

    @Test
    public void testDef() {
        final String source = "" +
                "def x\n" +
                "def y\n" +
                "1.5 => x\n" +
                "2.5 => y\n" +
                "x + y => SHOT\n";

        final Compiler compiler = new Compiler();
        final Program program = compiler.compile(source);
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
                "   i + 1 => i\n" +
                "   goto Loop if i < 100\n" +
                "i => SHOT\n";

        final Compiler compiler = new Compiler();
        final Program program = compiler.compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        runComplete(interpreter);

        assertThat(env.readShot()).isEqualTo(100);
    }

    private void runComplete(Interpreter interpreter) {
        try {
            while (interpreter.runToNextLabel()) {
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

        public void setX(double value) {
            this.x = value;
        }

        @Override
        public double readY() {
            return this.y;
        }

        public void setY(double value) {
            this.y = value;
        }

        @Override
        public double readDamage() {
            return this.damage;
        }

        public void setDamage(double value) {
            this.damage = value;
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
    }
}