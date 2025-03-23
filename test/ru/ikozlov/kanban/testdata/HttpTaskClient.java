package ru.ikozlov.kanban.testdata;

import com.google.gson.Gson;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTaskClient {
    private String host;
    private Gson gson;
    HttpClient client;
    HttpResponse.BodyHandler<String> bodyHandler;

    public HttpTaskClient(String host, Gson gson) {
        this.host = host;
        this.gson = gson;
        this.client = HttpClient.newHttpClient();
        this.bodyHandler = HttpResponse.BodyHandlers.ofString();
    }

    public HttpResponse<String> getTasks() throws IOException, InterruptedException {
        URI url = URI.create(host + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> getTaskById(int taskId) throws IOException, InterruptedException {
        URI url = URI.create(host + "/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> postTask(Task task) throws IOException, InterruptedException {
        URI url = URI.create(host + "/tasks");
        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> deleteTask(int taskId) throws IOException, InterruptedException {
        URI url = URI.create(host + "/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> getSubtasks() throws IOException, InterruptedException {
        URI url = URI.create(host + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> getSubtaskById(int subtaskId) throws IOException, InterruptedException {
        URI url = URI.create(host + "/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> postSubtask(Subtask subtask) throws IOException, InterruptedException {
        URI url = URI.create(host + "/subtasks");
        String json = gson.toJson(subtask);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> deleteSubtask(int subtaskId) throws IOException, InterruptedException {
        URI url = URI.create(host + "/subtasks/" + subtaskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> getEpics() throws IOException, InterruptedException {
        URI url = URI.create(host + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> getEpicById(int epicId) throws IOException, InterruptedException {
        URI url = URI.create(host + "/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> getEpicSubtasks(int epicId) throws IOException, InterruptedException {
        URI url = URI.create(host + "/epics/" + epicId + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> postEpic(Epic epic) throws IOException, InterruptedException {
        URI url = URI.create(host + "/epics");
        String json = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> deleteEpic(int epicId) throws IOException, InterruptedException {
        URI url = URI.create(host + "/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> getHistory() throws IOException, InterruptedException {
        URI url = URI.create(host + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, bodyHandler);
    }

    public HttpResponse<String> getPrioritized() throws IOException, InterruptedException {
        URI url = URI.create(host + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, bodyHandler);
    }
}
