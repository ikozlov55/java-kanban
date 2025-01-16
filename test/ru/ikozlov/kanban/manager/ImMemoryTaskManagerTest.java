package ru.ikozlov.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

class ImMemoryTaskManagerTest {
    ImMemoryTaskManager taskManager;

    @BeforeEach
    void setup() {
        taskManager = new ImMemoryTaskManager();
    }

    @Test
    void taskCreation() {
        String title = "Task 1";
        String description = "Task 1 description";
        Task.Status status = Task.Status.NEW;
        Task newTask = new Task(title, description, status);

        taskManager.createTask(newTask);
        Task task = taskManager.getTask(1);

        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals(title, task.getTitle());
        Assertions.assertEquals(description, task.getDescription());
        Assertions.assertEquals(status, task.getStatus());
    }

    @Test
    void noIdConflictOnTaskCreation() {
        String title = "Task 1";
        String description = "Task 1 description";
        Task.Status status = Task.Status.NEW;
        Task newTask = new Task(title, description, status);
        newTask.setId(99);

        taskManager.createTask(newTask);
        Task task = taskManager.getTask(1);

        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals(title, task.getTitle());
        Assertions.assertEquals(description, task.getDescription());
        Assertions.assertEquals(status, task.getStatus());
        Assertions.assertNull(taskManager.getTask(99));
    }

    @Test
    void epicCreation() {
        String title = "Epic 1";
        String description = "Epic 1 description";
        Task.Status status = Task.Status.NEW;
        Epic newEpic = new Epic(title, description);

        taskManager.createEpic(newEpic);
        Epic epic = taskManager.getEpic(1);

        Assertions.assertEquals(1, epic.getId());
        Assertions.assertEquals(title, epic.getTitle());
        Assertions.assertEquals(description, epic.getDescription());
        Assertions.assertEquals(status, epic.getStatus());
        Assertions.assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void noIdConflictOnEpicCreation() {
        String title = "Epic 1";
        String description = "Epic 1 description";
        Task.Status status = Task.Status.NEW;
        Epic newEpic = new Epic(title, description);
        newEpic.setId(99);

        taskManager.createEpic(newEpic);
        Epic epic = taskManager.getEpic(1);

        Assertions.assertEquals(1, epic.getId());
        Assertions.assertEquals(title, epic.getTitle());
        Assertions.assertEquals(description, epic.getDescription());
        Assertions.assertEquals(status, epic.getStatus());
        Assertions.assertTrue(epic.getSubtasks().isEmpty());
        Assertions.assertNull(taskManager.getEpic(99));
    }

    @Test
    void subtaskCreation() {
        Epic newEpic = new Epic("Epic 1", "Epic 1 description");
        newEpic.setId(1);
        taskManager.createEpic(newEpic);
        String title = "Subtask 1";
        String description = "Subtask 1 description";
        Task.Status status = Task.Status.NEW;
        Subtask newSubtask = new Subtask(title, description, status, newEpic);

        taskManager.createSubtask(newSubtask);
        Subtask subtask = taskManager.getSubtask(2);

        Assertions.assertEquals(2, subtask.getId());
        Assertions.assertEquals(title, subtask.getTitle());
        Assertions.assertEquals(description, subtask.getDescription());
        Assertions.assertEquals(status, subtask.getStatus());
        Assertions.assertEquals(newEpic, subtask.getEpic());
    }

    @Test
    void noIdConflictOnSubtaskCreation() {
        Epic newEpic = new Epic("Epic 1", "Epic 1 description");
        newEpic.setId(1);
        taskManager.createEpic(newEpic);
        String title = "Subtask 1";
        String description = "Subtask 1 description";
        Task.Status status = Task.Status.NEW;
        Subtask newSubtask = new Subtask(title, description, status, newEpic);
        newSubtask.setId(99);

        taskManager.createSubtask(newSubtask);
        Subtask subtask = taskManager.getSubtask(2);

        Assertions.assertEquals(2, subtask.getId());
        Assertions.assertEquals(title, subtask.getTitle());
        Assertions.assertEquals(description, subtask.getDescription());
        Assertions.assertEquals(status, subtask.getStatus());
        Assertions.assertEquals(newEpic, subtask.getEpic());
        Assertions.assertNull(taskManager.getSubtask(99));
    }
}