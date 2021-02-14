package net.smackem.jobotwar.web;

import com.google.common.base.Strings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import net.smackem.jobotwar.web.persist.DaoFactories;
import net.smackem.jobotwar.web.persist.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebApp implements AutoCloseable {
    private final static Logger log = LoggerFactory.getLogger(WebApp.class);
    private final Javalin app;
    private final PlayController playController;
    private final RobotController robotController;
    private final MatchController matchController;
    private final HikariDataSource dataSource;

    WebApp(int port) {
        this.dataSource = createDataSource();
        final DaoFactory daoFactory = DaoFactories.sql(createConnectionSupplier());
        this.playController = new PlayController();
        this.matchController = new MatchController(daoFactory.getMatchDao(), daoFactory.getRobotDao());
        this.robotController = new RobotController(daoFactory.getRobotDao());
        this.app = Javalin.create().start(port);
        app.routes(() -> {
            path("play", () -> post(this.playController::create));
            crud("robot/:robot-id", this.robotController);
            path("match", () -> {
                get(this.matchController::getAll);
                post(this.matchController::create);
                get(":match-id", ctx -> this.matchController.get(ctx, ctx.pathParam("match-id")));
            });
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

    private HikariDataSource createDataSource() {
        // Required VM args:
        // -Djdbc.drivers=org.postgresql.Driver
        // -Ddb.url=jdbc:postgresql://localhost/jobotwar?user=philip
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getProperty("db.url"));
        return new HikariDataSource(config);
    }

    private Supplier<Connection> createConnectionSupplier() {
        return () -> {
            try {
                return this.dataSource.getConnection();
            } catch (SQLException e) {
                log.error("error getting sql connection", e);
                throw new RuntimeException(e);
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
        this.dataSource.close();
    }
}
