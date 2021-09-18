package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.lang.Program;
import net.smackem.jobotwar.web.beans.CompileRequest;
import net.smackem.jobotwar.web.beans.CompileResult;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CompileController extends Controller {
    private final static Logger log = LoggerFactory.getLogger(CompileController.class);
    private final GameService gameService = new GameService();

    CompileController() {
        super(0); // select is not supported
    }

    public void compile(Context ctx) {
        log.info("compile: {}", ctx.fullUrl());
        final CompileRequest request = ctx.bodyAsClass(CompileRequest.class);
        final CompileResult result;
        try {
            final Program program = this.gameService.compileRobotProgram(request.robotName(), request.code(), request.language());
            result = new CompileResult(program.toString());
        } catch (GameService.CompilationException e) {
            log.warn("compile error: {} [{}]", e.getMessage(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
            return;
        }
        ctx.json(result);
    }
}
