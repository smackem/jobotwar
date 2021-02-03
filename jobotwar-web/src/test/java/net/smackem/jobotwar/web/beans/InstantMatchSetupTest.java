package net.smackem.jobotwar.web.beans;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.lang.Compiler;
import org.junit.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class InstantMatchSetupTest {

    @Test
    public void serializeShallow() {
        final InstantMatchSetup dto = new InstantMatchSetup()
                .boardWidth(100)
                .boardHeight(200)
                .maxDuration(Duration.ofMillis(1000));
        final String json = JavalinJson.toJson(dto);
        final InstantMatchSetup dto2 = JavalinJson.fromJson(json, InstantMatchSetup.class);
        assertThat(dto2).isEqualTo(dto);
    }

    @Test
    public void serializeDeep() {
        final InstantMatchSetup dto = new InstantMatchSetup()
                .boardWidth(100)
                .boardHeight(200)
                .maxDuration(Duration.ofMillis(1000))
                .addRobots(
                        new InstantMatchRobot("depp").language(Compiler.Language.V1),
                        new InstantMatchRobot("other")
                                .language(Compiler.Language.V2)
                                .code("state main() {}")
                                .x(123.5).y(234.5));
        final String json = JavalinJson.toJson(dto);
        final InstantMatchSetup dto2 = JavalinJson.fromJson(json, InstantMatchSetup.class);
        assertThat(dto2).isEqualTo(dto);
    }
}
