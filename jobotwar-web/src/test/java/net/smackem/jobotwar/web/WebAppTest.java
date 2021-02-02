package net.smackem.jobotwar.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class WebAppTest {

    private static final int port = 55666;
    private static final String baseUri = "http://localhost:" + port + "/";
    private WebApp remoteServer;

    @Before
    public void setUp() {
        this.remoteServer = new WebApp(port);
    }

    @After
    public void tearDown() {
        this.remoteServer.close();
    }

    @Test
    public void play() throws IOException, InterruptedException {
        final HttpClient http = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUri + "play"))
                .POST(HttpRequest.BodyPublishers.ofString("""
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
                        """))
                .build();
        final HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
    }
}