package net.smackem.jobotwar.web;

import com.google.common.base.Strings;
import io.javalin.Javalin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WebApp {
    public static void main(String[] args) {
        final String portStr = System.getProperty("http.port");
        final int port = Strings.isNullOrEmpty(portStr) ? 8666 : Integer.parseInt(portStr);
        final Javalin javalin = Javalin.create().start(port);
        javalin.get("/simulate", ctx -> ctx.json(new Contract("hepp")));
        loop();
        javalin.stop();
    }

    static class Contract {
        private String name;

        public Contract(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String value) {
            this.name = value;
        }
    }

    private static void loop() {
        System.out.println("Enter to quit...");
        try (final var reader = new BufferedReader(new InputStreamReader(System.in))) {
            reader.readLine();
        } catch (IOException ignored) {
            // won't happen
        }
    }
}
