package net.smackem.jobotwar.web;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.RobotBean;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class MatchControllerTest extends ControllerTest {

    private static final String shooterSource = """
            loop:
                AIM + 7 -> AIM -> RADAR
                goto loop unless RADAR < 0
            shoot:
                0 - RADAR -> SHOT
                AIM -> RADAR
                goto shoot if RADAR < 0 or RANDOM < 0.5
                goto loop
            """;

    @Test
    public void create() throws IOException, InterruptedException {
        HttpResponse<String> response = http.post("robot", new RobotBean("")
                .name("shooter")
                .code(shooterSource));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED_201);
        final String shooterId = response.headers().firstValue(HttpHeader.LOCATION.asString())
                .map(MatchControllerTest::getPathSuffix)
                .orElseThrow();
        response = http.post("robot", new RobotBean("")
                .name("target"));
        final String targetId = response.headers().firstValue(HttpHeader.LOCATION.asString())
                .map(MatchControllerTest::getPathSuffix)
                .orElseThrow();
        response = http.post("match", new MatchBean("")
                .boardWidth(800)
                .boardHeight(600)
                .maxDuration(Duration.ofMinutes(5))
                .addRobotIds(shooterId, targetId));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED_201);
        final String matchPath = response.headers().firstValue(HttpHeader.LOCATION.asString())
                .map(h -> URI.create(h).getPath())
                .orElseThrow();
        response = http.get(matchPath);
        final MatchBean bean = JavalinJson.fromJson(response.body(), MatchBean.class);
        assertThat(bean.outcome()).isEqualTo(SimulationResult.Outcome.WIN);
        assertThat(bean.winnerId()).isEqualTo(shooterId);
    }

    @Test
    public void getAll() {
    }

    @Test
    public void get() {
    }

    private static String getPathSuffix(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }
}