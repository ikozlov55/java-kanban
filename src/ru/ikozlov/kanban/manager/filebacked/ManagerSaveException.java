package ru.ikozlov.kanban.manager.filebacked;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }
}
