package net.smackem.jobotwar.web.jsh;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import io.javalin.plugin.json.JavalinJackson;
import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.lang.Compiler;
import net.smackem.jobotwar.persist.PersistableRobot;
import net.smackem.jobotwar.persist.PersistableRobots;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.PersistableBean;
import net.smackem.jobotwar.web.beans.RobotBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("WeakerAccess") // allow public access for jshell
public class Jsh {
    public static final HttpClient http = HttpClient.newHttpClient();
    public static String baseUri = "http://localhost:5666/";

    public static URI getUri(String path, String... queryParams) {
        final StringBuilder sb = new StringBuilder(baseUri + CharMatcher.is('/').trimFrom(path));
        final Splitter splitter = Splitter.on('=').omitEmptyStrings();
        if (queryParams.length > 0) {
            sb.append('?');
            boolean first = true;
            for (final String param : queryParams) {
                final List<String> tokens = splitter.splitToList(param);
                if (tokens.size() > 1) {
                    if (first == false) {
                        sb.append("&");
                    }
                    sb.append(tokens.get(0));
                    sb.append('=');
                    sb.append(URLEncoder.encode(tokens.get(1), StandardCharsets.UTF_8));
                    first = false;
                }
            }
        }
        return URI.create(sb.toString());
    }

    public static HttpResponse<String> post(String path, PersistableBean bean) throws IOException, InterruptedException {
        final String json = JavalinJson.toJson(bean);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> post(String path, String body) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> put(String path, PersistableBean bean) throws IOException, InterruptedException {
        final String json = JavalinJson.toJson(bean);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> put(String path, String body) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> get(String path, String... queryParams) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path, queryParams))
                .GET()
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> delete(String path) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .DELETE()
                .build();
        return http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static RobotBean[] getRobots() throws Exception {
        return JavalinJson.fromJson(get("robot").body(), RobotBean[].class);
    }

    public static MatchBean[] getMatches() throws Exception {
        return JavalinJson.fromJson(get("match").body(), MatchBean[].class);
    }

    public static void printJson(Object bean) throws JsonProcessingException {
        final String json = JavalinJackson.getObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(bean);
        System.out.println(json);
    }

    public static RobotBean loadRobotBean(Path filePath) throws IOException {
        final RobotBean bean = new RobotBean("");
        try (final InputStream is = Files.newInputStream(filePath)) {
            PersistableRobots.load(() -> new LocalPersistableRobot(bean), is);
        }
        return bean;
    }

    private static record LocalPersistableRobot(RobotBean bean) implements PersistableRobot {

        @Override
        public String getSourceCode() {
            return this.bean.code();
        }

        @Override
        public void setSourceCode(String value) {
            this.bean.code(value);
        }

        @Override
        public String getSourceCodeLanguage() {
            return this.bean.language().name();
        }

        @Override
        public void setSourceCodeLanguage(String value) {
            this.bean.language(Enum.valueOf(Compiler.Language.class, value));
        }

        @Override
        public String getBaseName() {
            return bean.name();
        }

        @Override
        public void setBaseName(String value) {
            bean.name(value);
        }
    }
}
