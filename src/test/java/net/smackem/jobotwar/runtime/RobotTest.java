package net.smackem.jobotwar.runtime;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class RobotTest {

    @Test
    public void setAimAngle() {
        final Robot robot = new Robot(1.0, 0, RuntimeProgram.EMPTY);
        assertThatIllegalArgumentException().isThrownBy(() -> robot.setAimAngle(360));
        assertThatCode(() -> robot.setAimAngle(359)).doesNotThrowAnyException();
    }

    @Test
    public void setRadarAngle() {
        final Robot robot = new Robot(1.0, 0, RuntimeProgram.EMPTY);
        assertThatIllegalArgumentException().isThrownBy(() -> robot.setRadarAngle(360));
        assertThatCode(() -> robot.setRadarAngle(359)).doesNotThrowAnyException();
    }

    @Test
    public void accelerate() {
        final Robot robot = new Robot(1.0, 0, RuntimeProgram.EMPTY);
        assertThat(robot.getActualSpeedX()).isEqualTo(0);
        assertThat(robot.getActualSpeedY()).isEqualTo(0);

        robot.setSpeedX(3);
        robot.setSpeedY(4);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(1);
        assertThat(robot.getActualSpeedY()).isEqualTo(1);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(2);
        assertThat(robot.getActualSpeedY()).isEqualTo(2);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(3);
        assertThat(robot.getActualSpeedY()).isEqualTo(3);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(3);
        assertThat(robot.getActualSpeedY()).isEqualTo(4);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(3);
        assertThat(robot.getActualSpeedY()).isEqualTo(4);

        robot.setSpeedX(2);
        robot.setSpeedY(2);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(2);
        assertThat(robot.getActualSpeedY()).isEqualTo(3);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(2);
        assertThat(robot.getActualSpeedY()).isEqualTo(2);
    }

    @Test
    public void accelerateFast() {
        final Robot robot = new Robot(10.0, 0, RuntimeProgram.EMPTY);
        robot.setSpeedX(15);
        robot.setSpeedY(8);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(10);
        assertThat(robot.getActualSpeedY()).isEqualTo(8);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(15);
        assertThat(robot.getActualSpeedY()).isEqualTo(8);

        robot.setSpeedX(-5);
        robot.setSpeedY(-10);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(5);
        assertThat(robot.getActualSpeedY()).isEqualTo(-2);

        robot.accelerate();
        assertThat(robot.getActualSpeedX()).isEqualTo(-5);
        assertThat(robot.getActualSpeedY()).isEqualTo(-10);
    }
}