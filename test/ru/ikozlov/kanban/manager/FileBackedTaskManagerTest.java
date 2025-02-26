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
    void taskDeletion() {
        Task task = new TaskBuilder(1).build();
        taskManager.createTask(task);
        taskManager.deleteTask(1);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertNull(loadedManager.getTask(1));
        Assertions.assertEquals(0, loadedManager.getAllTasks().size());
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
    void epicDeletion() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.deleteEpic(1);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertNull(loadedManager.getEpic(1));
        Assertions.assertEquals(0, loadedManager.getAllEpics().size());
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
    void subtaskDeletion() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(2, epic).build();
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtask(2);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertNull(loadedManager.getSubtask(2));
        Assertions.assertEquals(0, loadedManager.getAllSubtasks().size());
        Assertions.assertNotNull(loadedManager.getEpic(1));
        Assertions.assertEquals(1, loadedManager.getAllEpics().size());
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

        Assertions.assertEquals(2, subtasks.size());
        Assertions.assertEquals(subtask1, subtasks.get(0));
        Assertions.assertEquals(subtask2, subtasks.get(1));
    }

    @Test
    void epicWithSubtasksDeletion() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask1 = new SubtaskBuilder(2, epic).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic).build();
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteEpic(1);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertNull(loadedManager.getEpic(1));
        Assertions.assertEquals(0, loadedManager.getAllEpics().size());
        Assertions.assertNull(loadedManager.getSubtask(2));
        Assertions.assertEquals(0, loadedManager.getAllSubtasks().size());
    }

    @Test
    void correctEpicStatusOnLoading() {
        Epic epic1 = new EpicBuilder(1).build();
        taskManager.createEpic(epic1);
        Epic epic2 = new EpicBuilder(2).build();
        taskManager.createEpic(epic2);
        taskManager.createSubtask(new SubtaskBuilder(3, epic2).status(Task.Status.IN_PROGRESS).build());
        taskManager.createSubtask(new SubtaskBuilder(4, epic2).status(Task.Status.IN_PROGRESS).build());
        Epic epic3 = new EpicBuilder(5).build();
        taskManager.createEpic(epic3);
        taskManager.createSubtask(new SubtaskBuilder(6, epic3).status(Task.Status.DONE).build());
        taskManager.createSubtask(new SubtaskBuilder(7, epic3).status(Task.Status.DONE).build());
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(Task.Status.NEW, loadedManager.getEpic(1).getStatus());
        Assertions.assertEquals(Task.Status.IN_PROGRESS, loadedManager.getEpic(2).getStatus());
        Assertions.assertEquals(Task.Status.DONE, loadedManager.getEpic(5).getStatus());
    }

    @Test
    void emptyManagerLoading() {
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertTrue(loadedManager.getAllTasks().isEmpty());
        Assertions.assertTrue(loadedManager.getAllEpics().isEmpty());
        Assertions.assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void multipleTasksLoading() {
        taskManager.createTask(new TaskBuilder(1).build());
        taskManager.createTask(new TaskBuilder(2).build());
        taskManager.createTask(new TaskBuilder(3).build());
        Epic epic1 = new EpicBuilder(4).build();
        Epic epic2 = new EpicBuilder(5).build();
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtask1 = new SubtaskBuilder(6, epic1).build();
        Subtask subtask2 = new SubtaskBuilder(7, epic1).build();
        Subtask subtask3 = new SubtaskBuilder(8, epic1).build();
        Subtask subtask4 = new SubtaskBuilder(9, epic1).build();
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);
        taskManager.createSubtask(subtask4);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(3, loadedManager.getAllTasks().size());
        Assertions.assertEquals(2, loadedManager.getAllEpics().size());
        Assertions.assertEquals(4, loadedManager.getAllSubtasks().size());
    }

    @Test
    void nonConsecutiveIdLoading() {
        Task task1 = new TaskBuilder(1).build();
        Task task2 = new TaskBuilder(2).build();
        Task task3 = new TaskBuilder(3).build();
        Task task4 = new TaskBuilder(4).build();
        Task task5 = new TaskBuilder(5).build();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.deleteTask(2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.deleteTask(4);
        taskManager.createTask(task5);
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Assertions.assertEquals(3, loadedManager.getAllTasks().size());
        Assertions.assertEquals(task1, loadedManager.getTask(1));
        Assertions.assertNull(loadedManager.getTask(2));
        Assertions.assertEquals(task3, loadedManager.getTask(3));
        Assertions.assertNull(loadedManager.getTask(4));
        Assertions.assertEquals(task5, loadedManager.getTask(5));
    }

}
