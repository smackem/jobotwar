package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.web.beans.GameInfoBean;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfoController extends Controller {
    private static final Logger log = LoggerFactory.getLogger(InfoController.class);

    InfoController() {
        super(0);
    }

    public void getAll(@NotNull Context ctx) {
        log.info("get matches: {}", ctx.fullUrl());
        ctx.json(new GameInfoBean());
    }
}
