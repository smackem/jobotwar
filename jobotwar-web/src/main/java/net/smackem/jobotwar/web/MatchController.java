package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MatchController extends Controller<MatchBean> {

    private final BeanRepository<RobotBean> robotRepo;

    MatchController(BeanRepository<MatchBean> repository, BeanRepository<RobotBean> robotRepo) {
        super(repository);
        this.robotRepo = Objects.requireNonNull(robotRepo);
    }

    public void create(@NotNull Context ctx) {
    }

    public void getAll(@NotNull Context ctx) {
    }

    public void get(@NotNull Context ctx) {
    }
}
