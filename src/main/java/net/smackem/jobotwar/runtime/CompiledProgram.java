package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Interpreter;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.lang.RuntimeEnvironment;

import java.util.Random;

public class CompiledProgram implements RobotProgram {

    private final Robot robot;
    private final Program program;
    private final Interpreter interpreter;

    private CompiledProgram(Robot robot, Program program) {
        this.robot = robot;
        this.program = program;
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
                return robot.getRadarAngle();
            }

            @Override
            public void writeRadar(double value) {
                if (value < 0) {
                    value += 360;
                }
                robot.setAimAngle(value % 360);
            }

            @Override
            public double readSpeedX() {
                return robot.getSpeedX();
            }

            @Override
            public void writeSpeedX(double value) {
                robot.setSpeedX(value);
            }

            @Override
            public double readSpeedY() {
                return robot.getSpeedY();
            }

            @Override
            public void writeSpeedY(double value) {
                robot.setSpeedY(value);
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
                return Constants.MAX_ROBOT_SPEED - robot.getHealth();
            }

            @Override
            public double readShot() {
                return robot.getShot();
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
