package ru.ikozlov.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;
import ru.ikozlov.kanban.testdata.EpicBuilder;
import ru.ikozlov.kanban.testdata.SubtaskBuilder;
import ru.ikozlov.kanban.testdata.TaskBuilder;

import java.util.List;

class InMemoryHistoryManagerTest {
    InMemoryHistoryManager historyManager;

    @BeforeEach
    void setup() {
        historyManager = new InMemoryHistoryManager();
    }


    @Test
    void recordsAddition() {
        Task task = new TaskBuilder(1).build();
        Epic epic = new EpicBuilder(2).build();
        Subtask subtask = new SubtaskBuilder(3, epic).build();
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
        int taskNum = 15;
        Task[] tasks = new Task[taskNum];
        for (int i = 0; i < tasks.length; i++) {
            Task task = new TaskBuilder(i + 1).build();
            tasks[i] = task;
            historyManager.add(task);
        }
        List<Task> records = historyManager.getHistory();

        Assertions.assertEquals(taskNum, records.size());
        Assertions.assertEquals(records.getFirst(), tasks[tasks.length - 1]);
        Assertions.assertEquals(records.getLast(), tasks[0]);
    }

    @Test
    void recordsCopyOnAddition() {
        Task task = new TaskBuilder(1).build();
        historyManager.add(task);
        task.setTitle("new " + task.getTitle());
        task.setDescription("new " + task.getDescription());
        task.setStatus(Task.Status.IN_PROGRESS);
        Task record = historyManager.getHistory().getFirst();

        Assertions.assertNotEquals(task.getTitle(), record.getTitle());
        Assertions.assertNotEquals(task.getDescription(), record.getDescription());
        Assertions.assertNotEquals(task.getStatus(), record.getStatus());
    }

    @Test
    void recordsDontChangeOnAddition() {
        Task task = new TaskBuilder(1).build();
        historyManager.add(task);
        Task record = historyManager.getHistory().getFirst();

        Assertions.assertEquals(task.getId(), record.getId());
        Assertions.assertEquals(task.getTitle(), record.getTitle());
        Assertions.assertEquals(task.getDescription(), record.getDescription());
        Assertions.assertEquals(task.getStatus(), record.getStatus());
    }

    @Test
    void noDoublesOnMultipleAdditions() {
        Task task1 = new TaskBuilder(1).build();
        Task task2 = new TaskBuilder(2).build();
        Task task3 = new TaskBuilder(3).build();
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> records = historyManager.getHistory();

        Assertions.assertEquals(3, records.size());
    }

    @Test
    void orderOnMultipleAddition() {
        Task task1 = new TaskBuilder(1).build();
        Task task2 = new TaskBuilder(2).build();
        Task task3 = new TaskBuilder(3).build();
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> records = historyManager.getHistory();
        Assertions.assertEquals(task2, records.getFirst());
        Assertions.assertEquals(task1, records.get(1));
        Assertions.assertEquals(task3, records.getLast());
    }

    @Test
    void latestVersionStoredOnMultipleAddition() {
        Task task = new TaskBuilder(1).build();
        historyManager.add(task);
        task.setTitle("new " + task.getTitle());
        task.setDescription("new " + task.getDescription());
        task.setStatus(Task.Status.IN_PROGRESS);
        historyManager.add(task);
        Task record = historyManager.getHistory().getFirst();

        Assertions.assertEquals(task.getTitle(), record.getTitle());
        Assertions.assertEquals(task.getDescription(), record.getDescription());
        Assertions.assertEquals(task.getStatus(), record.getStatus());
    }

    @Test
    void recordsRemoval() {
        for (int i = 1; i <= 4; i++) {
            historyManager.add(new TaskBuilder(i).build());
        }
        historyManager.remove(2);
        historyManager.remove(1);
        historyManager.remove(4);
        historyManager.remove(3);

        Assertions.assertEquals(0, historyManager.getHistory().size());
    }
}