package net.smackem.jobotwar.web;

import io.javalin.Javalin;
import net.smackem.jobotwar.web.persist.DaoFactories;
import net.smackem.jobotwar.web.persist.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebApp implements AutoCloseable {
    private final static Logger log = LoggerFactory.getLogger(WebApp.class);
    private final Javalin app;
    private final PlayController playController;
    private final RobotController robotController;
    private final MatchController matchController;

    WebApp(int port) {
        final DaoFactory daoFactory = DaoFactories.inMemory();
        this.app = Javalin.create().start(port);
        this.playController = new PlayController();
        this.matchController = new MatchController(daoFactory.getMatchDao(), daoFactory.getRobotDao());
        this.robotController = new RobotController(daoFactory.getRobotDao());
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
        testDb();
//        final String portStr = System.getProperty("http.port");
//        final int port = Strings.isNullOrEmpty(portStr)
//                ? 8666
//                : Integer.parseInt(portStr);
//        try (final WebApp ignored = new WebApp(port)) {
//            loop();
//        }
    }

    private static void testDb() {
// VM args:
// -Djdbc.drivers=org.postgresql.Driver
// -Ddb.url=jdbc:postgresql://localhost/jobotwar?user=philip
//        try {
//            Class.forName("org.postgresql.Driver");
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        final String url = System.getProperty("db.url");
        try (final Connection conn = DriverManager.getConnection(url)) {
            final Statement stmt = conn.createStatement();
            final ResultSet rs = stmt.executeQuery("select id, name from robot");
            while (rs.next()) {
                System.out.printf("%s %s\n", rs.getString(1), rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
