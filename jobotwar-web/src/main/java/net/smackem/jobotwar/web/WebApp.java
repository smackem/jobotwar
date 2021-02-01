package net.smackem.jobotwar.web;

import com.google.common.base.Strings;
import io.javalin.Javalin;
import io.javalin.http.Context;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.CompiledProgram;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class WebApp implements AutoCloseable {
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
        final List<Robot> robots = body.robots().stream()
                .map(WebApp::buildRobot)
                .collect(Collectors.toList());
        final Board board = new Board(body.boardWidth(), body.boardHeight(), robots);
        final SimulationResult result = new SimulationRunner(board).runGame(body.maxDuration());
        ctx.json(new PlayResponse(result.outcome())
                .winner(result.winner().name())
                .duration(result.duration()));
    }

    private static Robot buildRobot(RobotDto dto) {
        return new Robot.Builder(r -> {
            final var result = new Compiler().compile(dto.code(), dto.language());
            return new CompiledProgram(r, result.program(), null);
        }).x(dto.x()).y(dto.y()).name(dto.name()).build();
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
