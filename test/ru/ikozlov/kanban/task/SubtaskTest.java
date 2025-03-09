package ru.ikozlov.kanban.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class SubtaskTest {
    @Test
    void subtaskInit() {
        Epic epic = new Epic("Epic 1", "Epic 1 description");
        epic.setId(999);
        int id = 1;
        String title = "Subtask 1";
        String description = "Subtask 1 description";
        Task.Status status = Task.Status.NEW;
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime startTime = LocalDateTime.now();
        Subtask subtask = new Subtask(title, description, status, epic, duration, startTime);
        subtask.setId(id);

        Assertions.assertEquals(id, subtask.getId());
        Assertions.assertEquals(title, subtask.getTitle());
        Assertions.assertEquals(description, subtask.getDescription());
        Assertions.assertEquals(status, subtask.getStatus());
        Assertions.assertEquals(epic, subtask.getEpic());
        Assertions.assertEquals(startTime, subtask.getStartTime());
        Assertions.assertEquals(startTime.plus(duration), subtask.getEndTime());
    }


    @Test
    void subtaskEquality() {
        Epic epic = new Epic("Epic 1", "Epic 1 description");
        epic.setId(999);
        Subtask subtask1 = new Subtask("Epic 1", "Epic 1 description", Task.Status.NEW, epic,
                Duration.ZERO, LocalDateTime.now());
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Epic 2", "Epic 2 description", Task.Status.NEW, epic,
                Duration.ZERO, LocalDateTime.now());
        subtask2.setId(1);
        Subtask subtask3 = new Subtask("Epic 1", "Epic 1 description", Task.Status.NEW, epic,
                Duration.ZERO, LocalDateTime.now());
        subtask3.setId(3);

        Assertions.assertEquals(subtask1, subtask2);
        Assertions.assertNotEquals(subtask1, subtask3);
        Assertions.assertEquals(0, subtask1.compareTo(subtask2));
        Assertions.assertTrue(subtask1.compareTo(subtask3) < 0);
    }
}