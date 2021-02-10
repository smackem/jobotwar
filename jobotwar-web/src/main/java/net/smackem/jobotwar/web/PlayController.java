package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.CompiledProgram;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.RobotProgramContext;
import net.smackem.jobotwar.runtime.simulation.SimulationEvent;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;
import net.smackem.jobotwar.web.beans.*;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayController extends Controller<PersistableBean> {
    private final static Logger log = LoggerFactory.getLogger(PlayController.class);
    private final GameService gameService = new GameService();

    PlayController() {
        super(null);
    }

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
