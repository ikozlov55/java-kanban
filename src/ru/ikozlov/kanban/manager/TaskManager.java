package ru.ikozlov.kanban.manager;

import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getPrioritizedTasks();

    List<Task> getAllTasks();

    void clearTasks();

    Task getTask(int id);

    Task createTask(Task task);

    Task updateTask(int id, Task task);

    Task deleteTask(int id);

    List<Epic> getAllEpics();

    void clearEpics();

    Epic getEpic(int id);

    Epic createEpic(Epic epic);

    Epic updateEpic(int id, Epic epic);

    Epic deleteEpic(int id);

    List<Subtask> getEpicSubtasks(int id);

    List<Subtask> getAllSubtasks();

    void clearSubtasks();

    Subtask getSubtask(int id);

    Subtask createSubtask(Subtask subtask);

    Subtask updateSubtask(int id, Subtask subtask);

    Subtask deleteSubtask(int id);

    List<Task> getHistory();

}