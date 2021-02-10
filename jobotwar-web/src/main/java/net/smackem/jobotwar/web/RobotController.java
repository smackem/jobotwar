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

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RobotController extends Controller<RobotBean> implements CrudHandler {

    private final GameService gameService = new GameService();

    RobotController(BeanRepository<RobotBean> repository) {
        super(repository);
    }

    @Override
    public void getAll(@NotNull Context ctx) {
        final Query query = createQuery(ctx);
        try {
            ctx.json(this.repository().select(query).collect(Collectors.toList()));
        } catch (ParseException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    @Override
    public void getOne(@NotNull Context ctx, @NotNull String id) {
        final List<RobotBean> result = this.repository().get(id);
        if (result.isEmpty()) {
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
        try {
            this.gameService.compileRobotProgram(bean.name(), bean.code(), bean.language());
            this.repository().put(bean.freeze());
        } catch (ConstraintViolationException | GameService.CompilationException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
        ctx.status(HttpStatus.CREATED_201).header(HttpHeader.LOCATION.asString(), ctx.url() + "/" + bean.id());
    }

    @Override
    public void update(@NotNull Context ctx, @NotNull String id) {
        final RobotBean bean = ctx.bodyAsClass(RobotBean.class);
        bean.dateModified(OffsetDateTime.now());
        if (bean.dateCreated() == null) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result("missing attribute 'dateCreated'");
            return;
        }
        try {
            this.gameService.compileRobotProgram(bean.name(), bean.code(), bean.language());
            this.repository().update(bean);
        } catch (NoSuchBeanException | GameService.CompilationException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull Context ctx, @NotNull String id) {
        if (this.repository().delete(id).isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND_404);
        }
    }
}
