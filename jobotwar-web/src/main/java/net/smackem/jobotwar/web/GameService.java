package net.smackem.jobotwar.web;

import com.google.common.base.Strings;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.CompiledProgram;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.RobotProgramContext;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;
import net.smackem.jobotwar.web.beans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {
    private final static Logger log = LoggerFactory.getLogger(GameService.class);

    public Program compileRobotProgram(String robotName, String code, Compiler.Language language) throws CompilationException {
        final Compiler compiler = new Compiler();
        final Compiler.Result result;
        try {
            result = compiler.compile(Strings.nullToEmpty(code), language);
        } catch (Exception e) {
            throw new CompilationException(robotName, e);
        }
        if (result.hasErrors()) {
            throw new CompilationException(robotName, result.errors());
        }
        return result.program();
    }

    public void playMatch(MatchBean match, Collection<RobotBean> robotBeans) throws CompilationException {
        final Collection<Robot> robots = buildRobots(robotBeans);
        final Board board = new Board(match.boardWidth(), match.boardHeight(), robots);
        board.disperseRobots();
        final OffsetDateTime now = OffsetDateTime.now();
        final SimulationResult result = new SimulationRunner(board).runGame(match.maxDuration());
        final String winnerId;
        final Robot winner = result.winner();
        if (winner != null) {
            winnerId = robotBeans.stream()
                    .filter(bean -> Objects.equals(bean.name(), winner.name()))
                    .map(RobotBean::id)
                    .findFirst()
                    .orElse(null);
        } else {
            winnerId = null;
        }
        match.duration(result.duration())
                .outcome(result.outcome())
                .dateStarted(now)
                .winnerId(winnerId)
                .addEvents(result.eventLog().stream()
                        .map(ev -> new MatchEvent(ev.gameTime().toMillis(), ev.event()))
                        .toArray(MatchEvent[]::new));
    }

    public InstantMatchResult playInstantMatch(InstantMatchSetup setup) throws CompilationException {
        final Collection<Robot> robots = buildInstantMatchRobots(setup.robots());
        final Board board = new Board(setup.boardWidth(), setup.boardHeight(), robots);
        final SimulationResult result = new SimulationRunner(board).runGame(setup.maxDuration());
        return new InstantMatchResult(result.outcome())
                .winner(result.winner() != null ? result.winner().name() : null)
                .duration(result.duration())
                .addEvents(result.eventLog().stream()
                        .map(ev -> new MatchEvent(ev.gameTime().toMillis(), ev.event()))
                        .toArray(MatchEvent[]::new));
    }

    private Collection<Robot> buildInstantMatchRobots(Collection<InstantMatchRobot> beans) throws CompilationException {
        final Collection<Robot> robots = new ArrayList<>(beans.size());
        final RobotProgramContext ctx = createRobotProgramContext();
        for (final InstantMatchRobot bean : beans) {
            final Program program = compileRobotProgram(bean.name(), bean.code(), bean.language());
            final Robot robot = new Robot.Builder(r -> new CompiledProgram(r, program, ctx))
                    .x(bean.x())
                    .y(bean.y())
                    .name(bean.name())
                    .build();
            robots.add(robot);
        }
        return robots;
    }

    private Collection<Robot> buildRobots(Collection<RobotBean> robotBeans) throws CompilationException {
        final Collection<Robot> robots = new ArrayList<>(robotBeans.size());
        final RobotProgramContext ctx = createRobotProgramContext();
        for (final RobotBean robotBean : robotBeans) {
            final Program program = compileRobotProgram(robotBean.name(), robotBean.code(), robotBean.language());
            final Robot robot = new Robot.Builder(r -> new CompiledProgram(r, program, ctx))
                    .name(robotBean.name())
                    .build();
            robots.add(robot);
        }
        return robots;
    }

    public static class CompilationException extends Exception {
        private CompilationException(String robotName, Collection<String> errors) {
            super("'%s' compilation error: %s".formatted(robotName, String.join("\n", errors)));
        }

        private CompilationException(String robotName, Exception cause) {
            super("'%s' compilation error: %s".formatted(robotName, cause.getMessage()), cause);
        }
    }

    private static RobotProgramContext createRobotProgramContext() {
        return new RobotProgramContext() {
            @Override
            public void logMessage(Robot robot, String category, double value) {
                log.info("{} {} {}", robot.name(), category, value);
            }

            @Override
            public double nextRandomDouble(Robot robot) {
                return ThreadLocalRandom.current().nextDouble();
            }
        };
    }
}
