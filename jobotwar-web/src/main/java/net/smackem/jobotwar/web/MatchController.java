package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.web.beans.IdGenerator;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.query.Query;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MatchController extends Controller<MatchBean> {
    private final static Logger log = LoggerFactory.getLogger(MatchController.class);
    private final BeanRepository<RobotBean> robotRepo;
    private final GameService gameService = new GameService();

    MatchController(BeanRepository<MatchBean> repository, BeanRepository<RobotBean> robotRepo) {
        super(repository);
        this.robotRepo = Objects.requireNonNull(robotRepo);
    }

    public void create(@NotNull Context ctx) {
        log.info("new match: {}", ctx.fullUrl());
        final MatchBean match = ctx.bodyAsClass(MatchBean.class);
        final List<RobotBean> robotBeans = this.robotRepo.get(match.robotIds().toArray(new String[0]));
        if (robotBeans.size() != match.robotIds().size()) {
            log.warn("expected {} robots, found {} in repo [{}]", robotBeans.size(), match.robotIds().size(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result("not all robots were found");
            return;
        }
        try {
            this.gameService.playMatch(match, robotBeans);
            this.repository().put(match.id(IdGenerator.next()).freeze());
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
            ctx.json(this.repository().select(query).collect(Collectors.toList()));
        } catch (ParseException e) {
            log.warn("error getting matches: {} [{}]", e.getMessage(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    public void get(@NotNull Context ctx) {
        final String id = ctx.splat(0);
        log.info("get single match (id = {}): {}", id, ctx.fullUrl());
        final List<MatchBean> result = this.repository().get(id);
        if (result.isEmpty()) {
            log.info("match {} not found [{}]", id, ctx.fullUrl());
            ctx.status(HttpStatus.NOT_FOUND_404);
            return;
        }
        ctx.json(result.get(0));
    }
}
