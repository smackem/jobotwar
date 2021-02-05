package net.smackem.jobotwar.web;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.web.beans.RobotBean;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RobotControllerTest extends ControllerTest {

    @Test
    public void create() throws IOException, InterruptedException {
        final HttpClient http = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(baseUri("robot"))
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                            "name":"loser",
                            "code":"state main() {}",
                            "language":"V2"
                        }
                        """))
                .build();
        final HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED_201);
        final Optional<String> location = response.headers().firstValue(HttpHeader.LOCATION.asString());
        assertThat(location).isPresent();

        // GET new robot
        final HttpResponse<String> getResponse = http.send(
                HttpRequest.newBuilder(URI.create(location.get())).build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK_200);
        assertThat(getResponse.body()).isNotNull().isNotBlank();
        final RobotBean bean = JavalinJson.fromJson(getResponse.body(), RobotBean.class);
        assertThat(bean.id()).isNotNull();
        assertThat(bean.name()).isEqualTo("loser");
        assertThat(bean.code()).isEqualTo("state main() {}");
        assertThat(bean.language()).isEqualTo(Compiler.Language.V2);
        assertThat(bean.dateCreated()).isNotNull();
        assertThat(bean.dateModified()).isNull();
    }

    @Test
    public void update() {
    }

    @Test
    public void delete() {
    }
}