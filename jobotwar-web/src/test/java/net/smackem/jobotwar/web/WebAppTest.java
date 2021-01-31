package net.smackem.jobotwar.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

public class WebAppTest {

    private static final int PORT = 55666;
    private WebApp remoteServer;

    @Before
    public void setUp() {
        this.remoteServer = new WebApp(PORT);
    }

    @After
    public void tearDown() {
        this.remoteServer.close();
    }

    @Test
    public void play() throws IOException, InterruptedException {
        final HttpClient http = HttpClient.newHttpClient();
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + PORT))
                .POST(HttpRequest.BodyPublishers.ofString("""
                        {
                            "boardWidth":100.0,
                            "boardHeight":80.0,
                            "robots":[
                                {
                                    "name":"loser",
                                    "code":"state main() {}",
                                    "language":"V2",
                                    "x":10.0,
                                    "y":10.0
                                }
                            ]
                        }
                        """))
                .build();
        final HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.body()).isNotNull();
    }
}