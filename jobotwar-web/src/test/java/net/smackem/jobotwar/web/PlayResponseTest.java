package net.smackem.jobotwar.web;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.runtime.simulation.SimulationResult;
import org.junit.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayResponseTest {

    @Test
    public void serializeShallow() {
        final PlayResponse dto = new PlayResponse(SimulationResult.Outcome.WIN)
                .winner("depp")
                .duration(Duration.ofSeconds(10));
        final String json = JavalinJson.toJson(dto);
        final PlayResponse dto2 = JavalinJson.fromJson(json, PlayResponse.class);
        assertThat(dto).isEqualTo(dto2);
    }

    @Test
    public void serializeDeep() {
        final PlayResponse dto = new PlayResponse(SimulationResult.Outcome.WIN)
                .winner("depp")
                .duration(Duration.ofSeconds(10))
                .addEvents(
                        new GameEventDto(100, "no one got killed"),
                        new GameEventDto(500, "someone else got killed"));
        final String json = JavalinJson.toJson(dto);
        final PlayResponse dto2 = JavalinJson.fromJson(json, PlayResponse.class);
        assertThat(dto).isEqualTo(dto2);
    }
}
