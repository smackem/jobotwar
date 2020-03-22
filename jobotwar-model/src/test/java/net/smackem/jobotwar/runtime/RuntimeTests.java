package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Program;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeTests {
    private RuntimeTests() { throw new IllegalAccessError(); }

    public static final RobotProgramContext DUMMY_CONTEXT = new RobotProgramContext() {
        @Override
        public void logMessage(Robot robot, String category, double value) {
        }

        @Override
        public double nextRandomDouble(Robot robot) {
            return ThreadLocalRandom.current().nextDouble();
        }
    };

    public static Program createShooter() {
        final String source = """
                loop:
                    AIM + 7 -> AIM -> RADAR
                    goto loop unless RADAR < 0
                shoot:
                    0 - RADAR -> SHOT
                    AIM -> RADAR
                    goto shoot if RADAR < 0 or RANDOM < 0.5
                    goto loop
                """;
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source, Compiler.Language.V1);
        assert result.hasErrors() == false;
        return result.program();
    }

    public static Program createDumbAss() {
        final String source = """
                loop:
                    AIM + 3 -> AIM
                    1000 -> SHOT
                    50 - RANDOM * 100 -> SPEEDX if AIM % 45 = 0
                    goto loop
                """;
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source, Compiler.Language.V1);
        assert result.hasErrors() == false;
        return result.program();
    }

    public static void assertRobotsBuiltEqual(Robot r1, Robot r2) {
        assertThat(r1.name()).isEqualTo(r2.name());
        assertThat(r1.position()).isEqualTo(r2.position());
        assertThat(r1.rgb()).isEqualTo(r2.rgb());
        assertThat(r1.program()).isInstanceOf(CompiledProgram.class);
        assertThat(r2.program()).isInstanceOf(CompiledProgram.class);
        assertThat(((CompiledProgram)r1.program()).program()).isEqualTo(((CompiledProgram)r2.program()).program());
        assertThat(r1.coolDownTicks()).isEqualTo(r2.coolDownTicks());
        assertThat(r1.acceleration()).isEqualTo(r2.acceleration());
    }
}
