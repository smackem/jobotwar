package net.smackem.jobotwar.web;

import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import net.smackem.jobotwar.web.beans.IdGenerator;
import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import net.smackem.jobotwar.web.query.Query;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RobotController extends Controller<RobotBean> implements CrudHandler {
    private static final Logger log = LoggerFactory.getLogger(RobotController.class);
    private final GameService gameService = new GameService();

    RobotController(BeanRepository<RobotBean> repository) {
        super(repository);
    }

    @Override
    public void getAll(@NotNull Context ctx) {
        log.info("get robots: {}", ctx.fullUrl());
        final Query query = createQuery(ctx);
        try {
            ctx.json(this.repository().select(query).collect(Collectors.toList()));
        } catch (ParseException e) {
            log.warn("error getting robots: {} [{}]", e.getMessage(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    @Override
    public void getOne(@NotNull Context ctx, @NotNull String id) {
        log.info("get single robot (id = {}): {}", id, ctx.fullUrl());
        final List<RobotBean> result = this.repository().get(id);
        if (result.isEmpty()) {
            log.info("robot {} not found [{}]", id, ctx.fullUrl());
            ctx.status(HttpStatus.NOT_FOUND_404);
            return;
        }
        ctx.json(result.get(0));
    }

    @Override
    public void create(@NotNull Context ctx) {
        final RobotBean bean = ctx.bodyAsClass(RobotBean.class);
        bean.id(IdGenerator.next());
        bean.dateCreated(OffsetDateTime.now());
        log.info("create robot (id = {}) @ {} [{}]", bean.id(), bean.dateCreated(), ctx.fullUrl());
        try {
            this.gameService.compileRobotProgram(bean.name(), bean.code(), bean.language());
            this.repository().put(bean.freeze());
        } catch (ConstraintViolationException | GameService.CompilationException e) {
            log.warn("error creating robot: {} [{}]", e.getMessage(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
        ctx.status(HttpStatus.CREATED_201).header(HttpHeader.LOCATION.asString(), ctx.url() + "/" + bean.id());
    }

    @Override
    public void update(@NotNull Context ctx, @NotNull String id) {
        log.info("update robot (id = {}): {}", id, ctx.fullUrl());
        final RobotBean bean = ctx.bodyAsClass(RobotBean.class);
        bean.dateModified(OffsetDateTime.now());
        if (bean.dateCreated() == null) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result("missing attribute 'dateCreated'");
            return;
        }
        try {
            this.gameService.compileRobotProgram(bean.name(), bean.code(), bean.language());
            this.repository().update(bean.id(id).freeze());
        } catch (NoSuchBeanException e) {
            log.warn("robot to update not found: {} [{}]", e.getMessage(), ctx.fullUrl());
            ctx.status(HttpStatus.NOT_FOUND_404);
        } catch (GameService.CompilationException e) {
            log.warn("error updating robot: {} [{}]", e.getMessage(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull Context ctx, @NotNull String id) {
        log.info("delete robot (id = {}): {}", id, ctx.fullUrl());
        if (this.repository().delete(id).isEmpty()) {
            log.warn("robot to delete not found (id = {}) [{}]", id, ctx.fullUrl());
            ctx.status(HttpStatus.NOT_FOUND_404);
        }
    }
}
