package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.CompiledProgram;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.RobotProgramContext;
import net.smackem.jobotwar.runtime.simulation.SimulationEvent;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;
import net.smackem.jobotwar.web.beans.InstantMatchResult;
import net.smackem.jobotwar.web.beans.InstantMatchRobot;
import net.smackem.jobotwar.web.beans.InstantMatchSetup;
import net.smackem.jobotwar.web.beans.MatchEvent;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayController extends Controller {
    private final static Logger log = LoggerFactory.getLogger(PlayController.class);

    public void create(Context ctx) {
        final InstantMatchSetup body = ctx.bodyAsClass(InstantMatchSetup.class);
        final Collection<Robot> robots;
        try {
            robots = buildRobots(body.robots());
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST_400);
            ctx.result(e.getMessage());
            return;
        }
        final Board board = new Board(body.boardWidth(), body.boardHeight(), robots);
        final SimulationResult result = new SimulationRunner(board).runGame(body.maxDuration());
        ctx.json(new InstantMatchResult(result.outcome())
                .winner(result.winner() != null ? result.winner().name() : null)
                .duration(result.duration())
                .addEvents(result.eventLog().stream()
                        .map(PlayController::mapGameEvent)
                        .toArray(MatchEvent[]::new)));
    }

    private static Collection<Robot> buildRobots(Collection<InstantMatchRobot> dtos) throws Exception {
        if (dtos.size() < 2) {
            throw new Exception("board must contain at least two robots");
        }
        final List<Robot> robots = new ArrayList<>(dtos.size());
        final Compiler compiler = new Compiler();
        final RobotProgramContext ctx = createRobotProgramContext();
        for (final InstantMatchRobot dto : dtos) {
            final Compiler.Result result = compiler.compile(dto.code(), dto.language());
            if (result.hasErrors()) {
                throw new Exception("robot %s compilation error: %s".formatted(dto.name(), String.join("\n", result.errors())));
            }
            final Robot robot = new Robot.Builder(r -> new CompiledProgram(r, result.program(), ctx))
                    .x(dto.x())
                    .y(dto.y())
                    .name(dto.name())
                    .build();
            robots.add(robot);
        }
        return robots;
    }

    private static MatchEvent mapGameEvent(SimulationEvent event) {
        return new MatchEvent((int) event.gameTime().toMillis(), event.event());
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
