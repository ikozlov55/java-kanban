package ru.ikozlov.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ikozlov.kanban.manager.filebacked.FileBackedTaskManager;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;
import ru.ikozlov.kanban.testdata.EpicBuilder;
import ru.ikozlov.kanban.testdata.SubtaskBuilder;
import ru.ikozlov.kanban.testdata.TaskBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileBackedTaskManagerTest {
    FileBackedTaskManager taskManager;
    File file;

    @BeforeEach
    void setup() throws IOException {
        file = File.createTempFile("test", ".csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void taskSavingAndLoading() {
        Task task = new TaskBuilder(1).build();
        taskManager.createTask(task);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task record = loadedManager.getTask(1);

        Assertions.assertEquals(task.getId(), record.getId());
        Assertions.assertEquals(task.getTitle(), record.getTitle());
        Assertions.assertEquals(task.getDescription(), record.getDescription());
        Assertions.assertEquals(task.getStatus(), record.getStatus());
    }

    @Test
    void epicSavingAndLoading() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Epic record = loadedManager.getEpic(1);

        Assertions.assertEquals(epic.getId(), record.getId());
        Assertions.assertEquals(epic.getTitle(), record.getTitle());
        Assertions.assertEquals(epic.getDescription(), record.getDescription());
        Assertions.assertEquals(epic.getStatus(), record.getStatus());
        Assertions.assertTrue(record.getSubtasks().isEmpty());
    }

    @Test
    void subtaskSavingAndLoading() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(2, epic).build();
        taskManager.createSubtask(subtask);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Subtask record = loadedManager.getSubtask(2);

        Assertions.assertEquals(2, subtask.getId());
        Assertions.assertEquals(subtask.getTitle(), record.getTitle());
        Assertions.assertEquals(subtask.getDescription(), record.getDescription());
        Assertions.assertEquals(subtask.getStatus(), record.getStatus());
        Assertions.assertEquals(epic, record.getEpic());
    }

    @Test
    void epicWithSubtasksSavingAndLoading() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask1 = new SubtaskBuilder(2, epic).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic).build();
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Epic record = loadedManager.getEpic(1);
        List<Subtask> subtasks = record.getSubtasks();

        Assertions.assertEquals(subtasks.size(), 2);
        Assertions.assertEquals(subtasks.get(0), subtask1);
        Assertions.assertEquals(subtasks.get(1), subtask2);
    }

}
