package net.smackem.jobotwar.web;

import org.junit.Test;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class PlayControllerTest extends ControllerTest {

    @Test
    public void play() throws IOException, InterruptedException {
        final HttpResponse<String> response = http.post("play", """
                        {
                            "boardWidth":500,
                            "boardHeight":500,
                            "robots":[
                                {
                                    "name":"loser",
                                    "code":"state main() {}",
                                    "language":"V2",
                                    "x":50.0,
                                    "y":50.0
                                },
                                {
                                    "name":"loser",
                                    "code":"500 -> SPEEDX",
                                    "language":"V1",
                                    "x":50.0,
                                    "y":100.0
                                }
                            ]
                        }
                        """);
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
    }
}