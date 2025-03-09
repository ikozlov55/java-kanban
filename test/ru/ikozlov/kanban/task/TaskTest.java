package ru.ikozlov.kanban.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {
    @Test
    void taskInit() {
        int id = 999;
        String title = "Task 1";
        String description = "Task 1 description";
        Task.Status status = Task.Status.NEW;
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime startTime = LocalDateTime.now();
        Task task = new Task(title, description, status, duration, startTime);
        task.setId(id);

        Assertions.assertEquals(id, task.getId());
        Assertions.assertEquals(title, task.getTitle());
        Assertions.assertEquals(description, task.getDescription());
        Assertions.assertEquals(status, task.getStatus());
        Assertions.assertEquals(duration, task.getDuration());
        Assertions.assertEquals(startTime, task.getStartTime());
        Assertions.assertEquals(startTime.plus(duration), task.getEndTime());
    }

    @Test
    void taskEquality() {
        Task task1 = new Task("Task 1", "Task 1 description", Task.Status.NEW, Duration.ofHours(1),
                LocalDateTime.now());
        task1.setId(1);
        Task task2 = new Task("Task 2", "Task 2 description", Task.Status.NEW, Duration.ofHours(1),
                LocalDateTime.now());
        task2.setId(1);
        Task task3 = new Task("Task 1", "Task 1 description", Task.Status.NEW, Duration.ofHours(1),
                LocalDateTime.now());
        task3.setId(3);

        Assertions.assertEquals(task1, task2);
        Assertions.assertNotEquals(task1, task3);
        Assertions.assertEquals(0, task1.compareTo(task2));
        Assertions.assertTrue(task1.compareTo(task3) < 0);
    }
}