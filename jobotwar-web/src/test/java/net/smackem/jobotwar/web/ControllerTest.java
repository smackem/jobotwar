package net.smackem.jobotwar.web;

import org.junit.After;
import org.junit.Before;

public class ControllerTest {
    private static final int port = 55666;
    private static final String baseUri = "http://localhost:" + port + "/";
    private WebApp remoteServer;
    RestClient http;

    @Before
    public void setUp() {
        this.remoteServer = new WebApp(port);
        this.http = new RestClient(baseUri);
    }

    @After
    public void tearDown() {
        this.remoteServer.close();
    }
}
