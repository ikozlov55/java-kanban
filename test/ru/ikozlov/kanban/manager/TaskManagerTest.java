package ru.ikozlov.kanban.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;
import ru.ikozlov.kanban.testdata.EpicBuilder;
import ru.ikozlov.kanban.testdata.SubtaskBuilder;
import ru.ikozlov.kanban.testdata.TaskBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;


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
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getTask(task.getId()));
    }

    @Test
    void taskRemoval() {
        Task task = new TaskBuilder(1).build();
        taskManager.createTask(task);
        taskManager.deleteTask(1);

        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getTask(1));
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
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getEpic(99));
    }

    @Test
    void epicRemoval() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.deleteEpic(1);

        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getEpic(1));
    }

    @Test
    void epicSubtasksRemovedWithEpic() {
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask1 = new SubtaskBuilder(2, epic.getId()).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic.getId()).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteEpic(1);

        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getEpic(1));
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtask(2));
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtask(3));
    }

    @Test
    void subtaskCreation() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(2, epic.getId()).build();
        taskManager.createSubtask(subtask);
        Subtask record = taskManager.getSubtask(2);

        Assertions.assertEquals(2, subtask.getId());
        Assertions.assertEquals(subtask.getTitle(), record.getTitle());
        Assertions.assertEquals(subtask.getDescription(), record.getDescription());
        Assertions.assertEquals(subtask.getStatus(), record.getStatus());
        Assertions.assertEquals(epic.getId(), record.getEpicId());
    }

    @Test
    void noIdConflictOnSubtaskCreation() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(99, epic.getId()).build();
        taskManager.createSubtask(subtask);
        Subtask record = taskManager.getSubtask(2);

        Assertions.assertEquals(2, record.getId());
        Assertions.assertEquals(subtask.getTitle(), record.getTitle());
        Assertions.assertEquals(subtask.getDescription(), record.getDescription());
        Assertions.assertEquals(subtask.getStatus(), record.getStatus());
        Assertions.assertEquals(epic.getId(), record.getEpicId());
        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtask(99));
    }

    @Test
    void subtaskRemoval() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(2, epic.getId()).build();
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtask(2);

        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtask(2));
        Assertions.assertTrue(taskManager.getEpicSubtasks(1).isEmpty());
    }

    @Test
    void epicSubtasksUpdatesOnSubtaskRemoval() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask1 = new SubtaskBuilder(2, epic.getId()).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic.getId()).build();
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
        taskManager.createSubtask(new SubtaskBuilder(2, epic.getId()).build());
        taskManager.createSubtask(new SubtaskBuilder(3, epic.getId()).build());
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.NEW, record.getStatus());
    }

    @Test
    void epicStatusIsInProgressWhenSubtasksAreInProgress() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubtaskBuilder(2, epic.getId()).build());
        taskManager.createSubtask(new SubtaskBuilder(3, epic.getId()).status(Task.Status.IN_PROGRESS).build());
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.IN_PROGRESS, record.getStatus());
    }

    @Test
    void epicStatusIsInProgressWhenSomeSubtasksAreDone() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubtaskBuilder(2, epic.getId()).status(Task.Status.IN_PROGRESS).build());
        taskManager.createSubtask(new SubtaskBuilder(3, epic.getId()).status(Task.Status.DONE).build());
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.IN_PROGRESS, record.getStatus());
    }

    @Test
    void epicStatusIsDoneWhenAllSubtasksAreDone() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(new SubtaskBuilder(2, epic.getId()).status(Task.Status.DONE).build());
        taskManager.createSubtask(new SubtaskBuilder(3, epic.getId()).status(Task.Status.DONE).build());
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.DONE, record.getStatus());
    }

    @Test
    void epicStatusUpdatesWhenSubtasksUpdates() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask = new SubtaskBuilder(2, epic.getId()).build();
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
        Subtask subtask1 = new SubtaskBuilder(2, epic.getId()).status(Task.Status.DONE).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic.getId()).build();
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.deleteSubtask(2);
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.NEW, record.getStatus());
    }

    @Test
    void epicStatusUpdatesWhenSubtaskCleared() {
        Epic epic = new EpicBuilder(1).build();
        taskManager.createEpic(epic);
        Subtask subtask1 = new SubtaskBuilder(2, epic.getId()).status(Task.Status.DONE).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic.getId()).status(Task.Status.IN_PROGRESS).build();
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.clearSubtasks();
        Epic record = taskManager.getEpic(1);

        Assertions.assertEquals(Task.Status.NEW, record.getStatus());
    }

    @Test
    void taskManagerAccessHistoryOnAdditions() {
        Task task = new TaskBuilder(1).build();
        Epic epic = new EpicBuilder(2).build();
        Subtask subtask = new SubtaskBuilder(3, epic.getId()).build();
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

    @Test
    void getPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new TaskBuilder(1).startTime(now).build();
        Task task2 = new TaskBuilder(2).startTime(null).build();
        Task task3 = new TaskBuilder(3).startTime(now.minusHours(1)).build();
        Task task4 = new TaskBuilder(4).startTime(now.plusHours(3)).build();
        Epic epic = new EpicBuilder(5).build();
        Subtask subtask1 = new SubtaskBuilder(6, epic.getId()).startTime(now.minusHours(2)).build();
        Subtask subtask2 = new SubtaskBuilder(7, epic.getId()).startTime(now.plusHours(5)).build();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        List<Task> records = taskManager.getPrioritizedTasks();

        Assertions.assertEquals(5, records.size());
        Assertions.assertEquals(List.of(subtask1, task3, task1, task4, subtask2), records);
    }

    @Test
    void prioritizedTasksChangesOnTaskUpdate() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new TaskBuilder(1).startTime(now).build();
        Task task2 = new TaskBuilder(2).startTime(now.plusHours(1)).build();
        Task task3 = new TaskBuilder(3).startTime(now.plusHours(2)).build();
        Task task4 = new TaskBuilder(4).startTime(now.plusHours(3)).build();
        Epic epic = new EpicBuilder(5).build();
        Subtask subtask1 = new SubtaskBuilder(6, epic.getId()).startTime(now.plusHours(4)).build();
        Subtask subtask2 = new SubtaskBuilder(7, epic.getId()).startTime(now.plusHours(5)).build();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        task2.setStartTime(null);
        task3.setStartTime(now.minusHours(1));
        subtask1.setStartTime(null);
        subtask2.setStartTime(now.minusHours(2));
        taskManager.updateTask(2, task2);
        taskManager.updateTask(3, task3);
        taskManager.updateSubtask(6, subtask1);
        taskManager.updateSubtask(7, subtask2);
        List<Task> records = taskManager.getPrioritizedTasks();

        Assertions.assertEquals(4, records.size());
        Assertions.assertEquals(List.of(subtask2, task3, task1, task4), records);
    }

    @Test
    void prioritizedTasksChangesOnTaskRemove() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new TaskBuilder(1).startTime(now).build();
        Task task2 = new TaskBuilder(2).startTime(now.plusHours(1)).build();
        Task task3 = new TaskBuilder(3).startTime(now.plusHours(2)).build();
        Task task4 = new TaskBuilder(4).startTime(now.plusHours(3)).build();
        Epic epic = new EpicBuilder(5).build();
        Subtask subtask1 = new SubtaskBuilder(6, epic.getId()).startTime(now.plusHours(4)).build();
        Subtask subtask2 = new SubtaskBuilder(7, epic.getId()).startTime(now.plusHours(5)).build();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.deleteTask(1);
        taskManager.deleteTask(3);
        taskManager.deleteSubtask(7);
        List<Task> records = taskManager.getPrioritizedTasks();

        Assertions.assertEquals(3, records.size());
        Assertions.assertEquals(List.of(task2, task4, subtask1), records);
    }

    @Test
    void taskNotCreatedIfInOtherTaskDuration() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new TaskBuilder(1).startTime(now).duration(Duration.ofHours(1)).build();
        Task task2 = new TaskBuilder(2).startTime(now.plusMinutes(30)).duration(Duration.ofMinutes(10)).build();
        taskManager.createTask(task1);

        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void taskNotCreatedIfStartsInOtherTaskDuration() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new TaskBuilder(1).startTime(now).duration(Duration.ofHours(1)).build();
        Task task2 = new TaskBuilder(2).startTime(now.plusMinutes(30)).duration(Duration.ofHours(2)).build();
        taskManager.createTask(task1);

        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void taskNotCreatedIfEndsInOtherTaskDuration() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new TaskBuilder(1).startTime(now).duration(Duration.ofHours(1)).build();
        Task task2 = new TaskBuilder(2).startTime(now.minusHours(1)).duration(Duration.ofMinutes(90)).build();
        taskManager.createTask(task1);

        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void taskNotCreatedIfDurationEqualsOtherTaskDuration() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new TaskBuilder(1).startTime(now).duration(Duration.ofHours(1)).build();
        Task task2 = new TaskBuilder(2).startTime(now).duration(Duration.ofHours(1)).build();
        taskManager.createTask(task1);

        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void subtaskNotCreatedIfIntersectsWithOtherSubtask() {
        LocalDateTime now = LocalDateTime.now();
        Epic epic = new EpicBuilder(1).build();
        Subtask subtask1 = new SubtaskBuilder(2, epic.getId()).startTime(now).duration(Duration.ofHours(1)).build();
        Subtask subtask2 = new SubtaskBuilder(3, epic.getId()).startTime(now.minusMinutes(30))
                .duration(Duration.ofHours(1)).build();
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);

        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createSubtask(subtask2));
    }

    @Test
    void subtaskNotCreatedIfIntersectsWithOtherTask() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new TaskBuilder(1).startTime(now).duration(Duration.ofHours(1)).build();
        Epic epic = new EpicBuilder(2).build();
        Subtask subtask1 = new SubtaskBuilder(3, epic.getId())
                .startTime(now.plusMinutes(30)).duration(Duration.ofHours(1)).build();
        taskManager.createTask(task1);
        taskManager.createEpic(epic);

        Assertions.assertThrows(TimeIntersectionException.class, () -> taskManager.createSubtask(subtask1));
    }
}
