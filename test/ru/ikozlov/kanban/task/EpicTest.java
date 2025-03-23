package ru.ikozlov.kanban.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

class EpicTest {
    @Test
    void epicInit() {
        int id = 999;
        String title = "Epic 1";
        String description = "Epic 1 description";
        Epic epic = new Epic(title, description);
        epic.setId(id);

        Assertions.assertEquals(id, epic.getId());
        Assertions.assertEquals(title, epic.getTitle());
        Assertions.assertEquals(description, epic.getDescription());
        Assertions.assertEquals(Task.Status.NEW, epic.getStatus());
        Assertions.assertTrue(epic.getSubtasks().isEmpty());
        Assertions.assertEquals(Duration.ZERO, epic.getDuration());
        Assertions.assertNull(epic.getStartTime());
        Assertions.assertNull(epic.getEndTime());
    }

    @Test
    void epicInitWithSubtasks() {
        int id = 999;
        String title = "Epic 1";
        String description = "Epic 1 description";
        Epic epic = new Epic(title, description);
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = startTime.plusHours(3);
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1 description", Task.Status.NEW, id,
                Duration.ofHours(1), startTime);
        subtask1.setId(1);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2 description", Task.Status.NEW, id,
                Duration.ofHours(1), startTime.plusHours(1));
        subtask2.setId(2);
        Subtask subtask3 = new Subtask("Subtask 3", "Subtask 3 description", Task.Status.NEW, id,
                Duration.ofHours(1), startTime.plusHours(2));
        subtask3.setId(3);
        List<Subtask> subtasks = List.of(subtask1, subtask2, subtask3);
        epic = new Epic(title, description, subtasks);
        epic.setId(id);

        Assertions.assertEquals(id, epic.getId());
        Assertions.assertEquals(title, epic.getTitle());
        Assertions.assertEquals(description, epic.getDescription());
        Assertions.assertEquals(Task.Status.NEW, epic.getStatus());
        Assertions.assertEquals(epic.getSubtasks().size(), subtasks.size());
        Assertions.assertEquals(startTime, epic.getStartTime());
        Assertions.assertEquals(endTime, epic.getEndTime());
        Assertions.assertEquals(Duration.ofHours(3), epic.getDuration());
    }


    @Test
    void epicEquality() {
        Epic epic1 = new Epic("Epic 1", "Epic 1 description");
        epic1.setId(1);
        Epic epic2 = new Epic("Epic 2", "Epic 2 description");
        epic2.setId(1);
        Epic epic3 = new Epic("Epic 1", "Epic 1 description");
        epic3.setId(3);

        Assertions.assertEquals(epic1, epic2);
        Assertions.assertNotEquals(epic1, epic3);
        Assertions.assertEquals(0, epic1.compareTo(epic2));
        Assertions.assertTrue(epic1.compareTo(epic3) < 0);
    }
}