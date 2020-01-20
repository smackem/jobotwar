package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.lang.Interpreter;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.lang.RuntimeEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;

public class CompiledProgram implements RobotProgram {

    private static final Logger log = LoggerFactory.getLogger(CompiledProgram.class);
    private final Robot robot;
    private final Interpreter interpreter;
    private final BiConsumer<String, Double> messageLogger;

    public CompiledProgram(Robot robot, Program program, BiConsumer<String, Double> messageLogger) {
        this.robot = Objects.requireNonNull(robot);
        this.interpreter = new Interpreter(Objects.requireNonNull(program), environment());
        this.messageLogger = Objects.requireNonNull(messageLogger);
    }

    @Override
    public boolean next() {
        try {
            return this.interpreter.runNext();
        } catch (Interpreter.StackException e) {
            log.error(String.format("Interpreter error: %s @ pc=%s (%s) in program:\n%s", e.getMessage(), e.pc(), e.instruction(), this.interpreter.program()), e);
        }
        return false;
    }

    private RuntimeEnvironment environment() {
        final Random random = new Random();
        return new RuntimeEnvironment() {
            @Override
            public double readAim() {
                return robot.getAimAngle();
            }

            @Override
            public void writeAim(double value) {
                if (value < 0) {
                    value += 360;
                }
                robot.setAimAngle(value % 360);
            }

            @Override
            public double readRadar() {
                final RadarBeam beam = robot.getLatestRadarBeam();
                if (beam == null) {
                    return 0;
                }
                if (beam.hitKind() == RadarBeamHitKind.ROBOT) {
                    return -(Vector.distance(beam.hitPosition(), robot.getPosition()));
                }
                return Vector.distance(beam.hitPosition(), robot.getPosition());
            }

            @Override
            public void writeRadar(double value) {
                if (value < 0) {
                    value += 360;
                }
                robot.setRadarAngle(value % 360);
            }

            @Override
            public double readSpeedX() {
                // normalize to -500 to 500
                return robot.getSpeedX() * Constants.MAX_ROBOT_GAME_SPEED / Constants.MAX_ROBOT_SPEED;
            }

            @Override
            public void writeSpeedX(double value) {
                // normalize to -500 to 500
                value = Math.max(Math.min(value, Constants.MAX_ROBOT_GAME_SPEED), -Constants.MAX_ROBOT_GAME_SPEED);
                robot.setSpeedX(value * Constants.MAX_ROBOT_SPEED / Constants.MAX_ROBOT_GAME_SPEED);
            }

            @Override
            public double readSpeedY() {
                // normalize to -500 to 500
                return robot.getSpeedY() * Constants.MAX_ROBOT_GAME_SPEED / Constants.MAX_ROBOT_SPEED;
            }

            @Override
            public void writeSpeedY(double value) {
                // normalize to -500 to 500
                value = Math.max(Math.min(value, Constants.MAX_ROBOT_GAME_SPEED), -Constants.MAX_ROBOT_GAME_SPEED);
                robot.setSpeedY(value * Constants.MAX_ROBOT_SPEED / Constants.MAX_ROBOT_GAME_SPEED);
            }

            @Override
            public double readX() {
                return robot.getX();
            }

            @Override
            public double readY() {
                return robot.getY();
            }

            @Override
            public double readDamage() {
                return Constants.MAX_HEALTH - robot.getHealth();
            }

            @Override
            public double readShot() {
                return robot.getCoolDownHoldOff();
            }

            @Override
            public void writeShot(double value) {
                robot.setShot((int)(value + 0.5));
            }

            @Override
            public double getRandom() {
                return random.nextDouble();
            }

            @Override
            public void log(String category, double value) {
                messageLogger.accept(category, value);
            }
        };
    }
}
