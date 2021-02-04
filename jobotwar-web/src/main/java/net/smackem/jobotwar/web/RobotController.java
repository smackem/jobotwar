package net.smackem.jobotwar.web;

import com.google.common.base.Strings;
import io.javalin.apibuilder.CrudHandler;
import io.javalin.http.Context;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.persist.NoSuchBeanException;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

public class RobotController extends Controller<RobotBean> implements CrudHandler {

    RobotController(BeanRepository<RobotBean> repository) {
        super(repository);
    }

    @Override
    public void getAll(@NotNull Context ctx) {
        ctx.json(this.repository().select().collect(Collectors.toList()));
    }

    @Override
    public void getOne(@NotNull Context ctx, @NotNull String id) {
        this.repository().get(id).ifPresentOrElse(ctx::json, () -> ctx.status(HttpStatus.NOT_FOUND_404));
    }

    @Override
    public void create(@NotNull Context ctx) {
        final RobotBean robot = ctx.bodyAsClass(RobotBean.class);
        robot.dateCreated(OffsetDateTime.now());
        if (compileRobotProgram(ctx, robot) == false) {
            return;
        }
        try {
            this.repository().put(robot.freeze());
        } catch (ConstraintViolationException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    @Override
    public void update(@NotNull Context ctx, @NotNull String id) {
        final RobotBean robot = ctx.bodyAsClass(RobotBean.class);
        robot.dateModified(OffsetDateTime.now());
        if (robot.dateCreated() == null) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result("missing attribute 'dateCreated'");
            return;
        }
        if (compileRobotProgram(ctx, robot) == false) {
            return;
        }
        try {
            this.repository().update(robot);
        } catch (NoSuchBeanException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    @Override
    public void delete(@NotNull Context ctx, @NotNull String id) {
        if (this.repository().delete(id).isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND_404);
        }
    }

    private static boolean compileRobotProgram(Context ctx, RobotBean robot) {
        final Compiler compiler = new Compiler();
        final Compiler.Result result = compiler.compile(Strings.nullToEmpty(robot.code()), robot.language());
        if (result.hasErrors()) {
            ctx.status(HttpStatus.BAD_REQUEST_400)
                    .result("compilation error: " + String.join("\n", result.errors()));
            return false;
        }
        return true;
    }
}
