package ru.ikozlov.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.ikozlov.kanban.manager.inmemory.InMemoryTaskManager;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;
import ru.ikozlov.kanban.testdata.EpicBuilder;
import ru.ikozlov.kanban.testdata.SubtaskBuilder;
import ru.ikozlov.kanban.testdata.TaskBuilder;

import java.util.List;

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
    void taskRemoval() {
        Task task = new TaskBuilder(1).build();
        taskManager.createTask(task);
        taskManager.deleteTask(1);

        Assertions.assertNull(taskManager.getTask(1));
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
    void epicRemoval() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.deleteEpic(1);

        Assertions.assertNull(taskManager.getEpic(1));
    }

    @Test
    void epicSubtasksRemovedWithEpic() {
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask1 = new SubtaskBuilder(2, epic).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteEpic(1);

        Assertions.assertNull(taskManager.getEpic(1));
        Assertions.assertNull(taskManager.getSubtask(2));
        Assertions.assertNull(taskManager.getSubtask(3));
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

    @Test
    void subtaskRemoval() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(2, epic).build();
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtask(2);

        Assertions.assertNull(taskManager.getSubtask(2));
        Assertions.assertTrue(taskManager.getEpicSubtasks(1).isEmpty());
    }

    @Test
    void epicSubtasksUpdatesOnSubtaskRemoval() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask1 = new SubtaskBuilder(2, epic).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic).build();
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtask(3);
        List<Subtask> subtasks = taskManager.getEpic(1).getSubtasks();

        Assertions.assertEquals(1, subtasks.size());
        Assertions.assertEquals(subtask1, subtasks.getFirst());
    }

    @Test
    void epicStatusIsNewWhenNoSubtasks() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.NEW, record.getStatus());
    }

    @Test
    void epicStatusIsNewWhenAllSubtasksAreNew() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubtaskBuilder(2, epic).build());
        taskManager.createSubtask(new SubtaskBuilder(3, epic).build());
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.NEW, record.getStatus());
    }

    @Test
    void epicStatusIsInProgressWhenSubtasksAreInProgress() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubtaskBuilder(2, epic).build());
        taskManager.createSubtask(new SubtaskBuilder(3, epic).status(Task.Status.IN_PROGRESS).build());
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.IN_PROGRESS, record.getStatus());
    }

    @Test
    void epicStatusIsInProgressWhenSomeSubtasksAreDone() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubtaskBuilder(2, epic).status(Task.Status.IN_PROGRESS).build());
        taskManager.createSubtask(new SubtaskBuilder(3, epic).status(Task.Status.DONE).build());
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.IN_PROGRESS, record.getStatus());
    }

    @Test
    void epicStatusIsDoneWhenAllSubtasksAreDone() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubtaskBuilder(2, epic).status(Task.Status.DONE).build());
        taskManager.createSubtask(new SubtaskBuilder(3, epic).status(Task.Status.DONE).build());
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.DONE, record.getStatus());
    }

    @Test
    void epicStatusUpdatesWhenSubtasksUpdates() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(2, epic).build();
        taskManager.createSubtask(subtask);
        subtask.setStatus(Task.Status.IN_PROGRESS);
        taskManager.updateSubtask(2, subtask);
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.IN_PROGRESS, record.getStatus());
    }

    @Test
    void epicStatusUpdatesWhenSubtaskRemoved() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask1 = new SubtaskBuilder(2, epic).status(Task.Status.DONE).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic).build();
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtask(2);
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.NEW, record.getStatus());
    }

    @Test
    void taskManagerAccessHistoryOnAdditions() {
        Task task = new TaskBuilder(1).build();
        Epic epic = new EpicBuilder(2).build();
        Subtask subtask = new SubtaskBuilder(3, epic).build();
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);
        List<Task> records = taskManager.getHistory();

        Assertions.assertEquals(0, records.size());

        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);
        records = taskManager.getHistory();

        Assertions.assertEquals(3, records.size());
        Assertions.assertEquals(subtask, records.getFirst());
        Assertions.assertEquals(task, records.getLast());
    }

}