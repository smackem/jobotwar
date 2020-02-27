package net.smackem.jobotwar.runtime;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RobotsTest {

    @Test
    public void fromTemplateFresh() {
        final Robot r1 = new Robot.Builder(r -> new CompiledProgram(r, RuntimeTests.createDumbAss(), RuntimeTests.DUMMY_CONTEXT))
                .name("robot 1")
                .acceleration(3)
                .rgb(0x112233)
                .coolDownTicks(123)
                .imageUrl("some_image_url")
                .x(50).y(50)
                .build();

        final Robot r2 = Robots.fromTemplate(r1, null);
        assertThat(r1).isNotEqualTo(r2);
        RuntimeTests.assertRobotsBuiltEqual(r1, r2);
        assertThat(r1.getHealth()).isEqualTo(r2.getHealth());
        assertThat(((CompiledProgram)r1.program()).context()).isEqualTo(((CompiledProgram)r2.program()).context());
    }

    @Test
    public void fromTemplateWithDifferentContext() {
        final Robot r1 = new Robot.Builder(r -> new CompiledProgram(r, RuntimeTests.createDumbAss(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        final RobotProgramContext newCtx = new RobotProgramContext() {
            @Override
            public void logMessage(Robot robot, String category, double value) {
            }

            @Override
            public double nextRandomDouble(Robot robot) {
                return 0;
            }
        };

        final Robot r2 = Robots.fromTemplate(r1, newCtx);
        RuntimeTests.assertRobotsBuiltEqual(r1, r2);
        assertThat(((CompiledProgram)r1.program()).context()).isNotEqualTo(((CompiledProgram)r2.program()).context());
        assertThat(((CompiledProgram)r2.program()).context()).isEqualTo(newCtx);
    }

    @Test
    public void fromTemplateDefault() {
        final Robot r1 = new Robot.Builder(r -> new CompiledProgram(r, RuntimeTests.createDumbAss(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        final Robot r2 = Robots.fromTemplate(r1, null);
        assertThat(r1).isNotEqualTo(r2);
        RuntimeTests.assertRobotsBuiltEqual(r1, r2);
        assertThat(r1.getCoolDownHoldOff()).isEqualTo(r2.getCoolDownHoldOff());
        assertThat(r1.getAimAngle()).isEqualTo(r2.getAimAngle());
        assertThat(r1.getRadarAngle()).isEqualTo(r2.getRadarAngle());
        assertThat(r1.getHealth()).isEqualTo(r1.getHealth());
        assertThat(r1.getShot()).isEqualTo(r2.getShot());
        assertThat(r1.getSpeedX()).isEqualTo(r2.getSpeedX());
        assertThat(r1.getSpeedY()).isEqualTo(r2.getSpeedY());
    }

    @Test
    public void fromTemplateDamaged() {
        final Robot r1 = new Robot.Builder(r -> new CompiledProgram(r, RuntimeTests.createDumbAss(), RuntimeTests.DUMMY_CONTEXT))
                .name("robot 1")
                .acceleration(3)
                .rgb(0x112233)
                .coolDownTicks(123)
                .imageUrl("some_image_url")
                .x(50).y(50)
                .build();

        r1.setHealth(0);

        final Robot r2 = Robots.fromTemplate(r1, null);
        assertThat(r1).isNotEqualTo(r2);
        RuntimeTests.assertRobotsBuiltEqual(r1, r2);
        assertThat(r1.getHealth()).isNotEqualTo(r2.getHealth());
        assertThat(r1.isDead()).isNotEqualTo(r2.isDead());
    }

    @Test
    public void fromTemplateModified() {
        final Robot r1 = new Robot.Builder(r -> new CompiledProgram(r, RuntimeTests.createDumbAss(), RuntimeTests.DUMMY_CONTEXT))
                .build();
        r1.setCoolDownHoldOff(12);
        r1.setAimAngle(120);
        r1.setRadarAngle(50.0);
        r1.setHealth(50);
        r1.setShot(40);
        r1.setSpeedX(7);
        r1.setSpeedY(8);
        final Robot r2 = Robots.fromTemplate(r1, null);
        assertThat(r1).isNotEqualTo(r2);
        RuntimeTests.assertRobotsBuiltEqual(r1, r2);
        assertThat(r1.getCoolDownHoldOff()).isNotEqualTo(r2.getCoolDownHoldOff());
        assertThat(r1.getAimAngle()).isNotEqualTo(r2.getAimAngle());
        assertThat(r1.getRadarAngle()).isNotEqualTo(r2.getRadarAngle());
        assertThat(r1.getHealth()).isNotEqualTo(r2.getHealth());
        assertThat(r1.getShot()).isNotEqualTo(r2.getShot());
        assertThat(r1.getSpeedX()).isNotEqualTo(r2.getSpeedX());
        assertThat(r1.getSpeedY()).isNotEqualTo(r2.getSpeedY());
    }
}