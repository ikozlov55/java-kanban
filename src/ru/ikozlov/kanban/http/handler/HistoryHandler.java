package ru.ikozlov.kanban.http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.ikozlov.kanban.manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handle(HttpExchange exchange, Endpoint endpoint) throws IOException {
        if (endpoint.type == Endpoint.Type.GET_HISTORY) {
            String response = gson.toJson(taskManager.getHistory());
            sendText(exchange, response, 200);
        }
    }
}
