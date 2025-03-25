package ru.ikozlov.kanban.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handle(HttpExchange exchange, Endpoint endpoint) throws IOException {
        switch (endpoint.type) {
            case GET_EPICS -> {
                String response = gson.toJson(taskManager.getAllEpics());
                sendText(exchange, response, 200);
            }
            case GET_EPIC_ID -> {
                Epic epic = taskManager.getEpic(endpoint.taskId);
                String response = gson.toJson(epic);
                sendText(exchange, response, 200);
            }
            case GET_EPIC_ID_SUBTASKS -> {
                List<Subtask> subtasks = taskManager.getEpicSubtasks(endpoint.taskId);
                String response = gson.toJson(subtasks);
                sendText(exchange, response, 200);
            }
            case POST_EPIC -> {
                try (InputStream is = exchange.getRequestBody()) {
                    String body = new String(is.readAllBytes());
                    Epic epic = gson.fromJson(body, Epic.class);
                    Epic responseEpic = epic.getId() != null
                            ? taskManager.updateEpic(epic.getId(), epic)
                            : taskManager.createEpic(epic);
                    sendText(exchange, gson.toJson(responseEpic), 201);
                }
            }
            case DELETE_EPIC_ID -> {
                Epic epic = taskManager.deleteEpic(endpoint.taskId);
                String response = epic != null ? gson.toJson(epic) : "";
                sendText(exchange, response, 200);
            }
        }
    }
}
