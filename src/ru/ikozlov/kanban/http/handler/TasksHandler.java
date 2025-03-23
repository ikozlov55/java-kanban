package ru.ikozlov.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.task.Task;

import java.io.IOException;
import java.io.InputStream;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handle(HttpExchange exchange, Endpoint endpoint) throws IOException {
        switch (endpoint.type) {
            case GET_TASKS -> {
                String response = gson.toJson(taskManager.getAllTasks());
                sendText(exchange, response, 200);
            }
            case GET_TASK_ID -> {
                Task task = taskManager.getTask(endpoint.taskId);
                String response = gson.toJson(task);
                sendText(exchange, response, 200);
            }
            case POST_TASK -> {
                try (InputStream is = exchange.getRequestBody()) {
                    String body = new String(is.readAllBytes());
                    Task task = gson.fromJson(body, Task.class);
                    Task responseTask;
                    if (task.getId() != null) {
                        responseTask = taskManager.updateTask(task.getId(), task);
                    } else {
                        responseTask = taskManager.createTask(task);
                    }
                    sendText(exchange, gson.toJson(responseTask), 201);
                }
            }
            case DELETE_TASK_ID -> {
                Task task = taskManager.deleteTask(endpoint.taskId);
                String response = task != null ? gson.toJson(task) : "";
                sendText(exchange, response, 200);
            }
        }
    }
}
