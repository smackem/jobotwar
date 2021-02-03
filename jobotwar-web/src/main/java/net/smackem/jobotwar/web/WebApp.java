package net.smackem.jobotwar.web;

import com.google.common.base.Strings;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebApp implements AutoCloseable {
    private final static Logger log = LoggerFactory.getLogger(WebApp.class);
    private final Javalin app;
    private final PlayController playController;
    private final RobotController robotController;

    WebApp(int port) {
        this.app = Javalin.create().start(port);
        this.playController = new PlayController();
        this.robotController = new RobotController();
        app.routes(() -> {
            path("play", () -> post(this.playController::create));
            crud("robot/:robot-id", this.robotController);
        });
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
