package net.smackem.jobotwar.web;

import org.junit.After;
import org.junit.Before;

import java.net.URI;

public class ControllerTest {
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

    protected URI baseUri(String path) {
        return URI.create(baseUri + path);
    }
}
