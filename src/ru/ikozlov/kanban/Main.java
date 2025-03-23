package ru.ikozlov.kanban;

import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.manager.inmemory.InMemoryTaskManager;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();
        manager.createTask(new Task("Task1", "Task1", Task.Status.NEW, Duration.ofHours(1),
                LocalDateTime.now()));
        manager.createTask(new Task("Task2", "Task2", Task.Status.NEW, Duration.ofHours(1),
                LocalDateTime.now().minusHours(2)));
        Epic epic1 = manager.createEpic(new Epic("Epic1", "Epic1"));
        manager.createSubtask(new Subtask("Subtask1", "Subtask1", Task.Status.NEW, epic1.getId(),
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1)));
        manager.createSubtask(new Subtask("Subtask2", "Subtask2", Task.Status.IN_PROGRESS, epic1.getId(),
                Duration.ofMinutes(90), LocalDateTime.now().plusHours(2)));
        manager.createSubtask(new Subtask("Subtask3", "Subtask3", Task.Status.DONE, epic1.getId(),
                Duration.ofMinutes(121), LocalDateTime.now().plusHours(5)));
        manager.createEpic(new Epic("Epic2", "Epic2"));

        manager.getTask(2);
        manager.getTask(1);
        manager.getEpic(7);
        System.out.println(manager.getHistory());

        manager.getSubtask(5);
        manager.getSubtask(6);
        manager.getEpic(3);
        manager.getTask(1);
        manager.getEpic(7);
        System.out.println(manager.getHistory());

        manager.deleteTask(1);
        System.out.println(manager.getHistory());

        manager.deleteEpic(3);
        System.out.println(manager.getHistory());
    }
}
