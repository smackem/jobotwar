package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Program;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class RuntimeTests {
    private RuntimeTests() { throw new IllegalAccessError(); }

    public static final Random RANDOM = new Random();
    public static final RobotProgramContext DUMMY_CONTEXT = new RobotProgramContext() {
        @Override
        public void logMessage(Robot robot, String category, double value) {
        }

        @Override
        public double nextRandomDouble(Robot robot) {
            return RANDOM.nextDouble();
        }
    };

    public static Program createShooter() {
        final String source = "" +
                "loop:\n" +
                "    AIM + 7 -> AIM -> RADAR\n" +
                "    goto loop unless RADAR < 0\n" +
                "shoot:\n" +
                "    0 - RADAR -> SHOT\n" +
                "    AIM -> RADAR\n" +
                "    goto shoot if RADAR < 0 or RANDOM < 0.5\n" +
                "    goto loop\n";
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(source, Compiler.Language.V1);
        assert result.hasErrors() == false;
        return result.program();
    }

    public static Program createDumbAss() {
        final String source = "" +
                "loop:\n" +
                "    AIM + 3 -> AIM\n" +
                "    1000 -> SHOT\n" +
                "    50 - RANDOM * 100 -> SPEEDX if AIM % 45 = 0\n" +
                "    goto loop\n";
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
