package net.smackem.jobotwar.web;

import io.javalin.http.Context;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.runtime.Board;
import net.smackem.jobotwar.runtime.CompiledProgram;
import net.smackem.jobotwar.runtime.Robot;
import net.smackem.jobotwar.runtime.RobotProgramContext;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.runtime.simulation.SimulationRunner;
import net.smackem.jobotwar.web.beans.IdGenerator;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.MatchEvent;
import net.smackem.jobotwar.web.beans.RobotBean;
import net.smackem.jobotwar.web.persist.BeanRepository;
import net.smackem.jobotwar.web.persist.ConstraintViolationException;
import net.smackem.jobotwar.web.query.Query;
import org.eclipse.jetty.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MatchController extends Controller<MatchBean> {
    private final static Logger log = LoggerFactory.getLogger(MatchController.class);
    private final BeanRepository<RobotBean> robotRepo;

    MatchController(BeanRepository<MatchBean> repository, BeanRepository<RobotBean> robotRepo) {
        super(repository);
        this.robotRepo = Objects.requireNonNull(robotRepo);
    }

    public void create(@NotNull Context ctx) {
        final MatchBean match = ctx.bodyAsClass(MatchBean.class);
        final List<RobotBean> robotBeans = this.robotRepo.get(match.robotIds().toArray(new String[0]));
        if (robotBeans.size() != match.robotIds().size()) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result("not all robots were found");
            return;
        }
        final Collection<Robot> robots;
        try {
            robots = buildRobots(robotBeans);
        } catch (Exception e) {
            ctx.status(HttpStatus.BAD_REQUEST_400);
            ctx.result(e.getMessage());
            return;
        }
        final Board board = new Board(match.boardWidth(), match.boardHeight(), robots);
        board.disperseRobots();
        final OffsetDateTime now = OffsetDateTime.now();
        final SimulationResult result = new SimulationRunner(board).runGame(match.maxDuration());
        final String winnerId;
        if (result.winner() != null) {
            winnerId = robotBeans.stream()
                    .map(RobotBean::name)
                    .filter(name -> Objects.equals(name, result.winner().name()))
                    .findFirst()
                    .orElse(null);
        } else {
            winnerId = null;
        }
        match.duration(result.duration())
                .outcome(result.outcome())
                .dateStarted(now)
                .winnerId(winnerId)
                .addEvents(result.eventLog().stream()
                        .map(ev -> new MatchEvent(ev.gameTime().toMillis(), ev.event()))
                        .toArray(MatchEvent[]::new))
                .id(IdGenerator.next());
        try {
            this.repository().put(match.freeze());
        } catch (ConstraintViolationException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    public void getAll(@NotNull Context ctx) {
        final Query query = createQuery(ctx);
        try {
            ctx.json(this.repository().select(query).collect(Collectors.toList()));
        } catch (ParseException e) {
            ctx.status(HttpStatus.BAD_REQUEST_400).result(e.getMessage());
        }
    }

    public void get(@NotNull Context ctx) {
        final String id = ctx.splat(0);
        final List<MatchBean> result = this.repository().get(id);
        if (result.isEmpty()) {
            ctx.status(HttpStatus.NOT_FOUND_404);
            return;
        }
        ctx.json(result.get(0));
    }

    private static Collection<Robot> buildRobots(Collection<RobotBean> beans) throws Exception {
        if (beans.size() < 2) {
            throw new Exception("board must contain at least two robots");
        }
        final List<Robot> robots = new ArrayList<>(beans.size());
        final Compiler compiler = new Compiler();
        final RobotProgramContext ctx = createRobotProgramContext();
        for (final RobotBean bean : beans) {
            final Compiler.Result result = compiler.compile(bean.code(), bean.language());
            if (result.hasErrors()) {
                throw new Exception("robot %s compilation error: %s".formatted(bean.name(), String.join("\n", result.errors())));
            }
            final Robot robot = new Robot.Builder(r -> new CompiledProgram(r, result.program(), ctx))
                    .name(bean.name())
                    .build();
            robots.add(robot);
        }
        return robots;
    }

    private static RobotProgramContext createRobotProgramContext() {
        return new RobotProgramContext() {
            @Override
            public void logMessage(Robot robot, String category, double value) {
                log.info("{} {} {}", robot.name(), category, value);
            }

            @Override
            public double nextRandomDouble(Robot robot) {
                return ThreadLocalRandom.current().nextDouble();
            }
        };
    }
}
