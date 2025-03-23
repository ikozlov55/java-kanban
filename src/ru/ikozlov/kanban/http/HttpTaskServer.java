package ru.ikozlov.kanban.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import ru.ikozlov.kanban.http.handler.*;
import ru.ikozlov.kanban.http.util.DurationTypeAdapter;
import ru.ikozlov.kanban.http.util.EpicAdapter;
import ru.ikozlov.kanban.http.util.LocalDateTimeAdapter;
import ru.ikozlov.kanban.manager.Managers;
import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        Gson gson = makeGson();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    public static Gson makeGson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter().nullSafe())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .create();
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}


class TasksListTypeToken extends TypeToken<List<Task>> {
}

class SubtasksListTypeToken extends TypeToken<List<Subtask>> {
}

class EpicsListTypeToken extends TypeToken<List<Epic>> {
}