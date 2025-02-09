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

class InMemoryTaskManagerTest {
    InMemoryTaskManager taskManager;

    @BeforeEach
    void setup() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void taskCreation() {
        Task task = new TaskBuilder(1).build();
        taskManager.createTask(task);
        Task record = taskManager.getTask(1);

        Assertions.assertEquals(task.getId(), record.getId());
        Assertions.assertEquals(task.getTitle(), record.getTitle());
        Assertions.assertEquals(task.getDescription(), record.getDescription());
        Assertions.assertEquals(task.getStatus(), record.getStatus());
    }

    @Test
    void noIdConflictOnTaskCreation() {
        Task task = new TaskBuilder(99).build();
        taskManager.createTask(task);
        Task record = taskManager.getTask(1);

        Assertions.assertEquals(1, record.getId());
        Assertions.assertEquals(task.getTitle(), record.getTitle());
        Assertions.assertEquals(task.getDescription(), record.getDescription());
        Assertions.assertEquals(task.getStatus(), record.getStatus());
        Assertions.assertNull(taskManager.getTask(task.getId()));
    }

    @Test
    void epicCreation() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(epic.getId(), record.getId());
        Assertions.assertEquals(epic.getTitle(), record.getTitle());
        Assertions.assertEquals(epic.getDescription(), record.getDescription());
        Assertions.assertEquals(epic.getStatus(), record.getStatus());
        Assertions.assertTrue(record.getSubtasks().isEmpty());
    }

    @Test
    void noIdConflictOnEpicCreation() {
        Epic epic = new EpicBuilder(99).build();
        taskManager.createEpic(epic);
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(1, record.getId());
        Assertions.assertEquals(epic.getTitle(), record.getTitle());
        Assertions.assertEquals(epic.getDescription(), record.getDescription());
        Assertions.assertEquals(epic.getStatus(), record.getStatus());
        Assertions.assertTrue(epic.getSubtasks().isEmpty());
        Assertions.assertNull(taskManager.getEpic(99));
    }

    @Test
    void subtaskCreation() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(2, epic).build();
        taskManager.createSubtask(subtask);
        Subtask record = taskManager.getSubtask(2);

        Assertions.assertEquals(2, subtask.getId());
        Assertions.assertEquals(subtask.getTitle(), record.getTitle());
        Assertions.assertEquals(subtask.getDescription(), record.getDescription());
        Assertions.assertEquals(subtask.getStatus(), record.getStatus());
        Assertions.assertEquals(epic, record.getEpic());
    }

    @Test
    void noIdConflictOnSubtaskCreation() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(99, epic).build();
        taskManager.createSubtask(subtask);
        Subtask record = taskManager.getSubtask(2);

        Assertions.assertEquals(2, record.getId());
        Assertions.assertEquals(subtask.getTitle(), record.getTitle());
        Assertions.assertEquals(subtask.getDescription(), record.getDescription());
        Assertions.assertEquals(subtask.getStatus(), record.getStatus());
        Assertions.assertEquals(epic, record.getEpic());
        Assertions.assertNull(taskManager.getSubtask(99));
    }
}