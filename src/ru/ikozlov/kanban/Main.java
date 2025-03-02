package ru.ikozlov.kanban;

import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.manager.inmemory.InMemoryTaskManager;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

public class Main {

    public static void main(String[] args) {
//        TaskManager manager = new InMemoryTaskManager();
//        manager.createTask(new Task("Task1", "Task1", Task.Status.NEW));
//        manager.createTask(new Task("Task2", "Task2", Task.Status.NEW));
//        Epic epic1 = manager.createEpic(new Epic("Epic1", "Epic1"));
//        manager.createSubtask(new Subtask("Subtask1", "Subtask1", Task.Status.NEW, epic1));
//        manager.createSubtask(new Subtask("Subtask2", "Subtask2", Task.Status.NEW, epic1));
//        manager.createSubtask(new Subtask("Subtask3", "Subtask3", Task.Status.NEW, epic1));
//        manager.createEpic(new Epic("Epic2", "Epic2"));
//
//        manager.getTask(2);
//        manager.getTask(1);
//        manager.getEpic(7);
//        System.out.println(manager.getHistory());
//
//        manager.getSubtask(5);
//        manager.getSubtask(6);
//        manager.getEpic(3);
//        manager.getTask(1);
//        manager.getEpic(7);
//        System.out.println(manager.getHistory());
//
//        manager.deleteTask(1);
//        System.out.println(manager.getHistory());
//
//        manager.deleteEpic(3);
//        System.out.println(manager.getHistory());
    }
}
