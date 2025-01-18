package ru.ikozlov.kanban.manager;

import ru.ikozlov.kanban.task.Task;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static final int HISTORY_CAPACITY = 10;
    private final Deque<Task> accessHistory = new ArrayDeque<>();

    @Override
    public List<Task> getHistory() {
        return accessHistory.stream().toList();
    }

    @Override
    public void add(Task task) {
        accessHistory.addFirst(task.copy());
        if (accessHistory.size() > HISTORY_CAPACITY) {
            accessHistory.removeLast();
        }
    }
}
