package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Interpreter;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.lang.RuntimeEnvironment;

import java.util.Random;

public class CompiledProgram implements RobotProgram {

    private final Robot robot;
    private final Interpreter interpreter;

    private CompiledProgram(Robot robot, Program program) {
        this.robot = robot;
        this.interpreter = new Interpreter(program, environment());
    }

    public static CompiledProgram compile(Robot robot, String source) {
        final Compiler compiler = new Compiler();
        final Program program = compiler.compile(source);
        return new CompiledProgram(robot, program);
    }

    @Override
    public boolean next() {
        try {
            return this.interpreter.runToNextLabel();
        } catch (Interpreter.StackException e) {
            e.printStackTrace();
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
                return robot.getSpeedX() * 500 / Constants.MAX_ROBOT_SPEED;
            }

            @Override
            public void writeSpeedX(double value) {
                // normalize to -500 to 500
                value = Math.max(Math.min(value, 500), -500);
                robot.setSpeedX(value * Constants.MAX_ROBOT_SPEED / 500.0);
            }

            @Override
            public double readSpeedY() {
                // normalize to -500 to 500
                return robot.getSpeedY() * 500 / Constants.MAX_ROBOT_SPEED;
            }

            @Override
            public void writeSpeedY(double value) {
                // normalize to -500 to 500
                value = Math.max(Math.min(value, 500), -500);
                robot.setSpeedY(value * Constants.MAX_ROBOT_SPEED / 500.0);
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
        };
    }
}