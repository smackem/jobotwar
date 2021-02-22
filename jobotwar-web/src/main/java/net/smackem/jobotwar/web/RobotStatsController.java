package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.web.persist.RobotDao;
import org.jetbrains.annotations.NotNull;

public class RobotStatsController extends Controller {

    private final RobotDao robotDao;

    RobotStatsController(long selectedRowCountLimit, RobotDao robotDao) {
        super(selectedRowCountLimit);
        this.robotDao = robotDao;
    }

    public void getAll(@NotNull Context ctx) {
    }

    public void get(@NotNull Context ctx, @NotNull String id) {
    }
}
