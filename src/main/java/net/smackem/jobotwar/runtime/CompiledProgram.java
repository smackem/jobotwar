package net.smackem.jobotwar.runtime;

import net.smackem.jobotwar.lang.Interpreter;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.lang.RuntimeEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * A {@link RobotProgram} that has been compiled from source code and executes by using a
 * {@link Interpreter}.
 */
public class CompiledProgram implements RobotProgram {

    private static final Logger log = LoggerFactory.getLogger(CompiledProgram.class);
    private final Robot robot;
    private final Interpreter interpreter;
    private final RobotProgramContext context;

    /**
     * Initializes a new {@link CompiledProgram}.
     * @param robot The {@link Robot} that is controlled by the program.
     * @param program The {@link Program} that has been compiled from source code.
     * @param ctx The {@link RobotProgramContext} to hook in.
     */
    public CompiledProgram(Robot robot, Program program, RobotProgramContext ctx) {
        this.robot = Objects.requireNonNull(robot);
        this.interpreter = new Interpreter(Objects.requireNonNull(program), environment());
        this.context = Objects.requireNonNull(ctx);
    }

    /**
     * @return The {@link Program} that has been compiled from source code.
     */
    public Program program() {
        return this.interpreter.program();
    }

    /**
     * @return The {@link RobotProgramContext} to hook in.
     */
    public RobotProgramContext context() {
        return this.context;
    }

    /**
     * {@inheritDoc}
     */
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
        return new RuntimeEnvironment() {
            @Override
            public double readAim() {
                return robot.getAimAngle();
            }

            @Override
            public void writeAim(double value) {
                while (value < 0) {
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
                    return -(Vector.distance(beam.hitPosition(), robot.position()));
                }
                return Vector.distance(beam.hitPosition(), robot.position());
            }

            @Override
            public void writeRadar(double value) {
                while (value < 0) {
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
                final int shot = (int)(value + 0.5);
                if (shot >= 0) {
                    robot.setShot(shot);
                }
            }

            @Override
            public double getRandom() {
                return context.nextRandomDouble(robot);
            }

            @Override
            public void log(String category, double value) {
                context.logMessage(robot, category, value);
            }
        };
    }
}
