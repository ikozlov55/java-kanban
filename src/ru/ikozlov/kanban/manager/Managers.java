package ru.ikozlov.kanban.manager;

public class Managers {
    public static TaskManager getDefault() {
        return new ImMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
