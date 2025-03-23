package ru.ikozlov.kanban.http.handler;

public class InternalError {
    private final String reason;

    public InternalError(String reason) {
        this.reason = reason;
    }
}
