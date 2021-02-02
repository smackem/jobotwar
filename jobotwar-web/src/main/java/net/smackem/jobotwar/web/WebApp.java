package net.smackem.jobotwar.web;

import com.google.common.base.Strings;
import io.javalin.Javalin;
import io.javalin.http.Context;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.runtime.*;
import net.smackem.jobotwar.runtime.simulation.SimulationEvent;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class WebApp implements AutoCloseable {
    private final static Logger log = LoggerFactory.getLogger(WebApp.class);
    private final Javalin app;

    WebApp(int port) {
        this.app = Javalin.create().start(port);
        app.post("/play", this::postPlay);
    }

    public static void main(String[] args) {
        final String portStr = System.getProperty("http.port");
        final int port = Strings.isNullOrEmpty(portStr)
                ? 8666
                : Integer.parseInt(portStr);
        try (final WebApp ignored = new WebApp(port)) {
            loop();
        }
    }

    private void postPlay(Context ctx) {
        final PlayRequest body = ctx.bodyAsClass(PlayRequest.class);
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
        ctx.json(new PlayResponse(result.outcome())
                .winner(result.winner() != null ? result.winner().name() : null)
                .duration(result.duration())
                .addEvents(result.eventLog().stream()
                    .map(WebApp::mapGameEvent)
                    .toArray(GameEventDto[]::new)));
    }

    private static Collection<Robot> buildRobots(Collection<RobotDto> dtos) throws Exception {
        if (dtos.size() < 2) {
            throw new Exception("board must contain at least two robots");
        }
        final List<Robot> robots = new ArrayList<>(dtos.size());
        final Compiler compiler = new Compiler();
        final RobotProgramContext ctx = createRobotProgramContext();
        for (final RobotDto dto : dtos) {
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

    private static GameEventDto mapGameEvent(SimulationEvent event) {
        return new GameEventDto((int) event.gameTime().toMillis(), event.event());
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

    private static void loop() {
        System.out.println("Enter to quit...");
        try (final var reader = new BufferedReader(new InputStreamReader(System.in))) {
            reader.readLine();
        } catch (IOException ignored) {
            // won't happen
        }
    }

    @Override
    public void close() {
        this.app.stop();
    }
}
