package ru.ikozlov.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.util.List;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void setup() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void recordsAddition() {
        Task task = new Task("Task", "Task description", Task.Status.NEW);
        task.setId(1);
        Epic epic = new Epic("Epic", "Epic description");
        epic.setId(2);
        Subtask subtask = new Subtask("Subtask", "Subtask description", Task.Status.NEW, epic);
        subtask.setId(3);

        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);
        List<Task> records = historyManager.getHistory();

        Assertions.assertEquals(3, records.size());
        Assertions.assertTrue(records.contains(task));
        Assertions.assertTrue(records.contains(epic));
        Assertions.assertTrue(records.contains(subtask));
    }


    @Test
    void recordsOrderAndCapacity() {
        Task[] tasks = new Task[InMemoryHistoryManager.HISTORY_CAPACITY + 1];
        for (int i = 0; i < tasks.length; i++) {
            Task task = new Task(String.format("Task %d", i + 1), String.format("Task %d description", i + 1),
                    Task.Status.NEW);
            task.setId(i + 1);
            tasks[i] = task;
            historyManager.add(task);
        }
        List<Task> records = historyManager.getHistory();

        Assertions.assertEquals(InMemoryHistoryManager.HISTORY_CAPACITY, records.size());
        Assertions.assertEquals(records.getFirst(), tasks[tasks.length - 1]);
        Assertions.assertEquals(records.getLast(), tasks[1]);
    }

    @Test
    void recordsCopyOnAddition() {
        int id = 1;
        String title = "Task 1";
        String description = "Task 1 description";
        Task.Status status = Task.Status.NEW;
        Task task = new Task(title, description, status);
        task.setId(id);

        historyManager.add(task);
        task.setTitle("new " + title);
        task.setDescription("new " + description);
        task.setStatus(Task.Status.IN_PROGRESS);
        Task record = historyManager.getHistory().getFirst();

        Assertions.assertNotEquals(task.getTitle(), record.getTitle());
        Assertions.assertNotEquals(task.getDescription(), record.getDescription());
        Assertions.assertNotEquals(task.getStatus(), record.getStatus());
    }

    @Test
    void recordsDontChangeOnAddition() {
        int id = 1;
        String title = "Task 1";
        String description = "Task 1 description";
        Task.Status status = Task.Status.NEW;
        Task task = new Task(title, description, status);
        task.setId(id);

        historyManager.add(task);
        Task record = historyManager.getHistory().getFirst();
        Assertions.assertEquals(task.getId(), record.getId());
        Assertions.assertEquals(task.getTitle(), record.getTitle());
        Assertions.assertEquals(task.getDescription(), record.getDescription());
        Assertions.assertEquals(task.getStatus(), record.getStatus());
    }
}