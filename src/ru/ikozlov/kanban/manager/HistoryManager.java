package ru.ikozlov.kanban.manager;

import ru.ikozlov.kanban.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}
