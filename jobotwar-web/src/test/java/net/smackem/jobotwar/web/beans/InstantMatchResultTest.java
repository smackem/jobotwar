package net.smackem.jobotwar.web.beans;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import org.junit.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class InstantMatchResultTest {

    @Test
    public void serializeShallow() {
        final InstantMatchResult dto = new InstantMatchResult(SimulationResult.Outcome.WIN)
                .winner("depp")
                .duration(Duration.ofSeconds(10));
        final String json = JavalinJson.toJson(dto);
        final InstantMatchResult dto2 = JavalinJson.fromJson(json, InstantMatchResult.class);
        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    public void serializeDeep() {
        final InstantMatchResult dto = new InstantMatchResult(SimulationResult.Outcome.WIN)
                .winner("depp")
                .duration(Duration.ofSeconds(10))
                .addEvents(
                        new MatchEvent(100, "no one got killed"),
                        new MatchEvent(500, "someone else got killed"));
        final String json = JavalinJson.toJson(dto);
        final InstantMatchResult dto2 = JavalinJson.fromJson(json, InstantMatchResult.class);
        assertThat(dto).isEqualTo(dto2);
    }
}
