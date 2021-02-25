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
import java.sql.SQLException;
import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebApp implements AutoCloseable {
    private final static Logger log = LoggerFactory.getLogger(WebApp.class);
    private final static long selectedRowCountLimit = 10_000;
    private final Javalin app;
    private final PlayController playController;
    private final RobotController robotController;
    private final MatchController matchController;
    private final RobotStatsController robotStatsController;
    private final DaoFactory daoFactory;

    WebApp(int port, DaoFactory daoFactory) {
        this.daoFactory = Objects.requireNonNull(daoFactory);
        this.playController = new PlayController();
        this.matchController = new MatchController(selectedRowCountLimit, daoFactory.getMatchDao(), daoFactory.getRobotDao());
        this.robotController = new RobotController(selectedRowCountLimit, daoFactory.getRobotDao());
        this.robotStatsController = new RobotStatsController(selectedRowCountLimit, daoFactory.getRobotDao());
        this.app = Javalin.create().start(port);
        this.app.routes(() -> {
            path("play", () ->
                post(this.playController::create));
            crud("robot/:robot-id", this.robotController);
            path("match", () -> {
                get(this.matchController::getAll);
                post(this.matchController::create);
                get(":match-id", ctx -> this.matchController.get(ctx, ctx.pathParam("match-id")));
            });
            path("robot_stats/win", () -> {
                get(this.robotStatsController::getAll);
                get(":robot-id", ctx ->
                        this.robotStatsController.get(ctx, ctx.pathParam("robot-id")));
                path("vs_count", () -> {
                    get(":count", ctx ->
                            this.robotStatsController.getAllVsCount(ctx,
                                    ctx.pathParam("count", Integer.class).get()));
                    get(":count/:robot-id", ctx ->
                            this.robotStatsController.getVsCount(ctx,
                                    ctx.pathParam("count", Integer.class).get(),
                                    ctx.pathParam("robot-id")));
                });
            });
        });
    }

    public static void main(String[] args) {
        final String portStr = System.getProperty("http.port");
        final int port = Strings.isNullOrEmpty(portStr)
                ? 8666
                : Integer.parseInt(portStr);
        try (final WebApp ignored = new WebApp(port, createSqlDaoFactory())) {
            loop();
        } catch (Exception e) {
            log.warn("error shutting down application", e);
        }
    }

    private static DaoFactory createSqlDaoFactory() {
        // Required VM args:
        // -Djdbc.drivers=org.postgresql.Driver
        // -Ddb.url=jdbc:postgresql://localhost/jobotwar?user=philip
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getProperty("db.url"));
        final HikariDataSource dataSource = new HikariDataSource(config);
        return DaoFactories.sql(() -> {
            try {
                return dataSource.getConnection();
            } catch (SQLException e) {
                log.error("error getting sql connection", e);
                throw new RuntimeException(e);
            }
        }, dataSource);
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
    public void close() throws Exception {
        this.app.stop();
        this.daoFactory.close();
    }
}
