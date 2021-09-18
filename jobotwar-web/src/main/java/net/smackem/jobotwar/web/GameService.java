package net.smackem.jobotwar.web;

import com.google.common.base.Strings;
import com.google.common.collect.Streams;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.runtime.*;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;
import net.smackem.jobotwar.web.beans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
        final Map<Robot, String> robots = buildRobots(robotBeans);
        final Map<String, MatchRobot> matchRobotsById = match.robots().stream()
                .collect(Collectors.toMap(MatchRobot::robotId, r -> r));
        final Board board = new Board(match.boardWidth(), match.boardHeight(), robots.keySet());
        board.disperseRobots();
        // store robot positions in match.robots
        for (final Robot r : board.robots()) {
            final String robotId = robots.get(r);
            final MatchRobot matchRobot = matchRobotsById.get(robotId);
            matchRobot.x(r.getX()).y(r.getY());
        }
        // run simulation
        final OffsetDateTime now = OffsetDateTime.now();
        final SimulationResult result = new SimulationRunner(board).runGame(match.maxDuration());
        final Robot winner = result.winner();
        final String winnerId = winner != null
                ? robots.get(result.winner())
                : null;
        log.info("match outcome: {}, winner: {}", result.outcome(), winner != null ? winner.name() : null);
        // store simulation outcome in match
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
        final Collection<MatchFrame> frames = new ArrayList<>();
        final SimulationResult result = new SimulationRunner(board).runGame(
                setup.maxDuration(),
                tick -> frames.add(createFrameFromTickResult(board, tick)));
        return new InstantMatchResult(result.outcome())
                .winner(result.winner() != null ? result.winner().name() : null)
                .duration(result.duration())
                .addEvents(result.eventLog().stream()
                        .map(ev -> new MatchEvent(ev.gameTime().toMillis(), ev.event()))
                        .toArray(MatchEvent[]::new))
                .addFrames(frames.toArray(MatchFrame[]::new));
    }

    private static MatchFrame createFrameFromTickResult(Board board, GameEngine.TickResult tick) {
        return new MatchFrame(0)
                .addRobots(board.robots().stream()
                        .map(r -> new RobotVisual(r.getX(), r.getY(), r.name(), r.getHealth(), r.getAimAngle()))
                        .toArray(RobotVisual[]::new))
                .addProjectiles(board.projectiles().stream()
                        .map(p -> new ProjectileVisual(p.position().x(), p.position().y()))
                        .toArray(ProjectileVisual[]::new))
                .addRadarBeams(tick.radarBeams().stream()
                        .map(rb -> new RadarBeamVisual(
                                rb.sourceRobot().name(),
                                rb.hitPosition().x(),
                                rb.hitPosition().y(),
                                rb.hitKind()))
                        .toArray(RadarBeamVisual[]::new))
                .addExplosions(Streams.concat(
                        tick.collisionPositions().stream().map(v -> new ExplosionVisual(v.x(), v.y(), ExplosionVisualKind.COLLISION)),
                        tick.explodedProjectiles().stream().map(p -> new ExplosionVisual(p.position().x(), p.position().y(), ExplosionVisualKind.COLLISION)),
                        tick.killedRobots().stream().map(r -> new ExplosionVisual(r.getX(), r.getY(), ExplosionVisualKind.KILLED_ROBOT)))
                        .toArray(ExplosionVisual[]::new));
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

    private Map<Robot, String> buildRobots(Collection<RobotBean> robotBeans) throws CompilationException {
        final Map<Robot, String> robots = new HashMap<>();
        final RobotProgramContext ctx = createRobotProgramContext();
        for (final RobotBean robotBean : robotBeans) {
            final Program program = compileRobotProgram(robotBean.name(), robotBean.code(), robotBean.language());
            final Robot robot = new Robot.Builder(r -> new CompiledProgram(r, program, ctx))
                    .name(robotBean.name())
                    .build();
            robots.put(robot, robotBean.id());
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
                log.debug("{} {} {}", robot.name(), category, value);
            }

            @Override
            public double nextRandomDouble(Robot robot) {
                return ThreadLocalRandom.current().nextDouble();
            }
        };
    }
}
