package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.web.beans.InstantMatchResult;
import net.smackem.jobotwar.web.beans.InstantMatchSetup;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayController extends Controller {
    private final static Logger log = LoggerFactory.getLogger(PlayController.class);
    private final GameService gameService = new GameService();

    PlayController() {}

    public void create(Context ctx) {
        log.info("play instant match: {}", ctx.fullUrl());
        final InstantMatchSetup setup = ctx.bodyAsClass(InstantMatchSetup.class);
        final InstantMatchResult result;
        try {
            result = this.gameService.playInstantMatch(setup);
        } catch (GameService.CompilationException e) {
            log.warn("error playing instant match: {} [{}]", e.getMessage(), ctx.fullUrl());
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
            return;
        }
        ctx.json(result);
    }
}
