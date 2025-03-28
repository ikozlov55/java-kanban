package ru.ikozlov.kanban.http.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.ikozlov.kanban.http.util.DurationTypeAdapter;
import ru.ikozlov.kanban.http.util.EpicAdapter;
import ru.ikozlov.kanban.http.util.LocalDateTimeAdapter;
import ru.ikozlov.kanban.manager.NotFoundException;
import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.manager.TimeIntersectionException;
import ru.ikozlov.kanban.task.Epic;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = makeGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Endpoint endpoint = Endpoint.create(method, path);
            if (endpoint.type == Endpoint.Type.UNKNOWN) {
                sendNotFound(exchange);
            }
            handle(exchange, endpoint);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (TimeIntersectionException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendText(exchange, gson.toJson(new InternalError(e.getMessage())), 500);
        }
    }

    protected abstract void handle(HttpExchange exchange, Endpoint endpoint) throws IOException;

    protected void sendText(HttpExchange exchange, String responseText, int statusCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(statusCode, 0);
            os.write(responseText.getBytes(DEFAULT_CHARSET));
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(406, 0);
        exchange.close();
    }

    public static Gson makeGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .create();
    }
}
