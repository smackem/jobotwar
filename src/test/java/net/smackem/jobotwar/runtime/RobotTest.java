package net.smackem.jobotwar.runtime;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class RobotTest {

    @Test
    public void setAimAngle() {
        final Robot robot = new Robot(1.0, RobotProgram.EMPTY);
        assertThatIllegalArgumentException().isThrownBy(() -> robot.setAimAngle(360));
        assertThatCode(() -> robot.setAimAngle(359)).doesNotThrowAnyException();
    }

    @Test
    public void setRadarAngle() {
        final Robot robot = new Robot(1.0, RobotProgram.EMPTY);
        assertThatIllegalArgumentException().isThrownBy(() -> robot.setRadarAngle(360));
        assertThatCode(() -> robot.setRadarAngle(359)).doesNotThrowAnyException();
    }
}