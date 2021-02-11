package net.smackem.jobotwar.web;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import io.javalin.plugin.json.JavalinJson;
import net.smackem.jobotwar.web.beans.PersistableBean;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * REST client class used for jshell and unit tests.
 */
public class RestClient {
    private final String baseUri;
    private final HttpClient http;

    public RestClient(String baseUri) {
        this.baseUri = Objects.requireNonNull(baseUri);
        this.http = HttpClient.newHttpClient();
    }

    public HttpClient client() {
        return this.http;
    }

    public URI getUri(String path, String... queryParams) {
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

    public HttpResponse<String> post(String path, PersistableBean bean) throws IOException, InterruptedException {
        final String json = JavalinJson.toJson(bean);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return this.http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> post(String path, String body) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return this.http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> patch(String path, PersistableBean bean) throws IOException, InterruptedException {
        final String json = JavalinJson.toJson(bean);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .build();
        return this.http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> put(String path, PersistableBean bean) throws IOException, InterruptedException {
        final String json = JavalinJson.toJson(bean);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return this.http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> put(String path, String body) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return this.http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> get(String path, String... queryParams) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path, queryParams))
                .GET()
                .build();
        return this.http.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> delete(String path) throws IOException, InterruptedException {
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(getUri(path))
                .DELETE()
                .build();
        return this.http.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
