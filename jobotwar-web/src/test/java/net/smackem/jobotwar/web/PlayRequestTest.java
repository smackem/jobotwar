package net.smackem.jobotwar.web;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.lang.Compiler;
import org.junit.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayRequestTest {

    @Test
    public void serializeShallow() {
        final PlayRequest dto = new PlayRequest()
                .boardWidth(100)
                .boardHeight(200)
                .maxDuration(Duration.ofMillis(1000));
        final String json = JavalinJson.toJson(dto);
        final PlayRequest dto2 = JavalinJson.fromJson(json, PlayRequest.class);
        assertThat(dto2).isEqualTo(dto);
    }

    @Test
    public void serializeDeep() {
        final PlayRequest dto = new PlayRequest()
                .boardWidth(100)
                .boardHeight(200)
                .maxDuration(Duration.ofMillis(1000))
                .addRobots(
                        new RobotDto("depp").language(Compiler.Language.V1),
                        new RobotDto("other")
                                .language(Compiler.Language.V2)
                                .code("state main() {}")
                                .x(123.5).y(234.5));
        final String json = JavalinJson.toJson(dto);
        final PlayRequest dto2 = JavalinJson.fromJson(json, PlayRequest.class);
        assertThat(dto2).isEqualTo(dto);
    }
}
