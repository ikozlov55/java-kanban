package ru.ikozlov.kanban.manager;

import ru.ikozlov.kanban.task.Task;

import java.util.List;

public interface HistoryManager {
    public void add(Task task);

    public List<Task> getHistory();
}
