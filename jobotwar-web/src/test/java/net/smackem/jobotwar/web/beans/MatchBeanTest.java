package net.smackem.jobotwar.web.beans;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import org.junit.Test;

import java.time.Duration;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class MatchBeanTest {
    @Test
    public void serializeFull() {
        final MatchBean bean = new MatchBean("2002")
                .boardHeight(800)
                .boardWidth(600)
                .dateStarted(OffsetDateTime.now())
                .maxDuration(Duration.ofSeconds(8))
                .duration(Duration.ofSeconds(5))
                .outcome(SimulationResult.Outcome.WIN)
                .addRobots(new MatchRobot("1001"), new MatchRobot("1002"))
                .addEvents(
                        new MatchEvent(100, "nothing happened"),
                        new MatchEvent(2000, "2002 got killed"))
                .winnerId("1001");
        final String json = JavalinJson.toJson(bean);
        final MatchBean bean2 = JavalinJson.fromJson(json, MatchBean.class);
        assertThat(bean2).isEqualTo(bean);
    }

    @Test
    public void serializeRequest() {
        final MatchBean bean = new MatchBean("2002")
                .boardHeight(800)
                .boardWidth(600)
                .maxDuration(Duration.ofSeconds(8))
                .addRobots(new MatchRobot("1001"), new MatchRobot("1002"))
                .freeze();
        final String json = JavalinJson.toJson(bean);
        final MatchBean bean2 = JavalinJson.fromJson(json, MatchBean.class);
        assertThat(bean2).isEqualTo(bean);
    }
}
