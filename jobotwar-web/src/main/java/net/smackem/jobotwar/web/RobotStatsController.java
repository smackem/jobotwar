package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.web.beans.RobotWinStats;
import net.smackem.jobotwar.web.persist.RobotDao;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class RobotStatsController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(RobotStatsController.class);
    private final RobotDao robotDao;

    RobotStatsController(long selectedRowCountLimit, RobotDao robotDao) {
        super(selectedRowCountLimit);
        this.robotDao = robotDao;
    }

    public void getAll(@NotNull Context ctx) {
        log.info("get robot win stats: {}", ctx.fullUrl());
        ctx.json(this.robotDao.getWinStats(createQuery(ctx)));
    }

    public void get(@NotNull Context ctx, @NotNull String id) {
        log.info("get robot win stats: {}", ctx.fullUrl());
        final Optional<RobotWinStats> winStats = this.robotDao.getWinStats(id);
        if (winStats.isEmpty()) {
            log.info("stats for robot {} not found [{}]", id, ctx.fullUrl());
            ctx.status(HttpStatus.NOT_FOUND_404);
            return;
        }
        ctx.json(winStats.get());
    }

    public void getAllVsCount(@NotNull Context ctx, @NotNull Integer count) {
        log.info("get robot win stats in matches with {} robots: {}", count, ctx.fullUrl());
        ctx.json(this.robotDao.getWinStatsVsCount(count, createQuery(ctx)));
    }

    public void getVsCount(@NotNull Context ctx, @NotNull Integer count, @NotNull String id) {
        log.info("get robot win stats in matches with {} robots: {}", count, ctx.fullUrl());
        final Optional<RobotWinStats> winStats = this.robotDao.getWinStatsVsCount(count, id);
        if (winStats.isEmpty()) {
            log.info("stats for robot {} not found [{}]", id, ctx.fullUrl());
            ctx.status(HttpStatus.NOT_FOUND_404);
            return;
        }
        ctx.json(winStats.get());
    }
}
