package ru.ikozlov.kanban.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.task.Subtask;

import java.io.IOException;
import java.io.InputStream;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handle(HttpExchange exchange, Endpoint endpoint) throws IOException {
        switch (endpoint.type) {
            case GET_SUBTASKS -> {
                String response = gson.toJson(taskManager.getAllSubtasks());
                sendText(exchange, response, 200);
            }
            case GET_SUBTASK_ID -> {
                Subtask subtask = taskManager.getSubtask(endpoint.taskId);
                String response = gson.toJson(subtask);
                sendText(exchange, response, 200);
            }
            case POST_SUBTASK -> {
                try (InputStream is = exchange.getRequestBody()) {
                    String body = new String(is.readAllBytes());
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    Subtask responseSubtask = subtask.getId() != null
                            ? taskManager.updateSubtask(subtask.getId(), subtask)
                            : taskManager.createSubtask(subtask);
                    sendText(exchange, gson.toJson(responseSubtask), 201);
                }
            }
            case DELETE_SUBTASK_ID -> {
                Subtask subtask = taskManager.deleteSubtask(endpoint.taskId);
                String response = subtask != null ? gson.toJson(subtask) : "";
                sendText(exchange, response, 200);
            }
        }
    }
}
