package net.smackem.jobotwar.web;

import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.web.beans.RobotBean;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class RobotControllerTest extends ControllerTest {

    private static final String path = "robot";

    @Test
    public void create() throws IOException, InterruptedException {
        final HttpResponse<String> response = http.post(path, """
                        {
                            "name":"loser",
                            "code":"state main() {}",
                            "language":"V2"
                        }
                        """);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED_201);
        final Optional<String> location = response.headers().firstValue(HttpHeader.LOCATION.asString());
        assertThat(location).isPresent();

        // GET new robot
        final HttpResponse<String> getResponse = http.client().send(
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
    public void update() throws IOException, InterruptedException {
        // insert new robot -> bean
        final RobotBean bean = new RobotBean("").name("depp");
        HttpResponse<String> response = http.post(path, bean);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED_201);
        final Optional<String> location = response.headers().firstValue(HttpHeader.LOCATION.asString());
        assertThat(location).isPresent();
        // get new robot -> bean2
        final String path = URI.create(location.get()).getPath();
        response = http.get(path);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        final RobotBean bean2 = JavalinJson.fromJson(response.body(), RobotBean.class);
        // update robot -> bean2
        response = http.patch(path, bean2.code("state main() {}").language(Compiler.Language.V2));
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        // get updated robot -> bean3
        response = http.get(path);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        final RobotBean bean3 = JavalinJson.fromJson(response.body(), RobotBean.class);
        assertThat(bean.name()).isEqualTo(bean3.name());
        assertThat(bean2.id()).isEqualTo(bean3.id());
        assertThat(bean2.code()).isEqualTo(bean3.code());
        assertThat(bean2.language()).isEqualTo(bean3.language());
        assertThat(bean3.dateModified()).isNotNull().isNotEqualTo(bean2.dateModified());
    }

    @Test
    public void delete() throws IOException, InterruptedException {
        final RobotBean bean = new RobotBean("").name("depp");
        HttpResponse<String> response = http.post(path, bean);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED_201);
        final Optional<String> location = response.headers().firstValue(HttpHeader.LOCATION.asString());
        assertThat(location).isPresent();
        final String path = URI.create(location.get()).getPath();
        response = http.delete(path);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK_200);
        response = http.get(path);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND_404);
    }
}