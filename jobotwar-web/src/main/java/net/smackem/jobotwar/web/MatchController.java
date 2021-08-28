package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.runtime.Constants;
import net.smackem.jobotwar.web.beans.IdGenerator;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.MatchRobot;
import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.MatchDao;
import net.smackem.jobotwar.web.persist.RobotDao;
import net.smackem.jobotwar.web.query.Query;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

public class MatchController extends Controller {
    private static final Logger log = LoggerFactory.getLogger(MatchController.class);
    private final MatchDao matchDao;
    private final RobotDao robotDao;
    private final GameService gameService = new GameService();

    MatchController(long selectedRowCountLimit, MatchDao matchDao, RobotDao robotDao) {
        super(selectedRowCountLimit);
        this.matchDao = Objects.requireNonNull(matchDao);
        this.robotDao = Objects.requireNonNull(robotDao);
    }

    public void create(@NotNull Context ctx) {
        final MatchBean match = ctx.bodyAsClass(MatchBean.class).gameVersion(Constants.GAME_VERSION);
        match.id(IdGenerator.next());
        log.info("create match (id = {}) [{}]", match.id(), ctx.fullUrl());
        final List<RobotBean> robotBeans = this.robotDao.get(match.robots().stream()
                .map(MatchRobot::robotId)
                .toArray(String[]::new));
        if (robotBeans.size() != match.robots().size()) {
            log.warn("expected {} robots, found {} in repo [{}]", match.robots().size(), robotBeans.size(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result("not all robots were found");
            return;
        }
        try {
            this.gameService.playMatch(match, robotBeans);
            this.matchDao.put(match.id(IdGenerator.next()).freeze());
        } catch (GameService.CompilationException | ConstraintViolationException e) {
            log.warn("error playing & storing match: {} [{}]", e.getMessage(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
            return;
        }
        ctx.status(HttpStatus.CREATED_201).header(HttpHeader.LOCATION.asString(), ctx.url() + "/" + match.id());
    }

    public void getAll(@NotNull Context ctx) {
        log.info("get matches: {}", ctx.fullUrl());
        final Query query = createQuery(ctx);
        try {
            ctx.json(this.matchDao.select(query));
        } catch (ParseException e) {
            log.warn("error getting matches: {} [{}]", e.getMessage(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    public void get(@NotNull Context ctx, @NotNull String id) {
        log.info("get single match (id = {}): {}", id, ctx.fullUrl());
        final List<MatchBean> result = this.matchDao.get(id);
        if (result.isEmpty()) {
            log.info("match {} not found [{}]", id, ctx.fullUrl());
            ctx.status(HttpStatus.NOT_FOUND_404);
            return;
        }
        ctx.json(result.get(0));
    }
}
