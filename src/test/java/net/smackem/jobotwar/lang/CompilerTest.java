package net.smackem.jobotwar.lang;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class CompilerTest {

    @Test
    public void testRegisters() {
        final String source = "" +
                "// hepp\n" +
                "START:\n" +
                "goto START if 100 < 0\n";
        final Compiler compiler = new Compiler();
        final Program program = compiler.compile(source);
        final TestEnvironment env = new TestEnvironment();
        final Interpreter interpreter = new Interpreter(program, env);

        assertThat(source).isNotNull();
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