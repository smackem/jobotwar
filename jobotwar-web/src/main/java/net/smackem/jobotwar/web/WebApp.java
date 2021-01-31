package net.smackem.jobotwar.web;

import com.google.common.base.Strings;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebApp implements AutoCloseable {
    private final Javalin app;

    private WebApp(int port) {
        this.app = Javalin.create().start(port);
        app.get("/play", this::onPlay);
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

    private void onPlay(Context ctx) {
        //final PlayMessageBody body = ctx.bodyAsClass(PlayMessageBody.class);
        ctx.json(new PlayMessageBody()
                .boardWidth(100)
                .boardHeight(80)
                .robots(new RobotDto("loser")
                        .code("state main() {}")
                        .x(10)
                        .y(10)));
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
