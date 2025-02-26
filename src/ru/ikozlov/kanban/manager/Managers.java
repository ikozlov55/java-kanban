package ru.ikozlov.kanban.manager;

import ru.ikozlov.kanban.manager.inmemory.InMemoryHistoryManager;
import ru.ikozlov.kanban.manager.inmemory.InMemoryTaskManager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
