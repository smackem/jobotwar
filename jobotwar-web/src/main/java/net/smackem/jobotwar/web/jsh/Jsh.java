package net.smackem.jobotwar.web.jsh;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.web.beans.MatchBean;
import net.smackem.jobotwar.web.beans.PersistableBean;
import net.smackem.jobotwar.web.beans.RobotBean;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
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
}
