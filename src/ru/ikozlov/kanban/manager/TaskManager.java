package ru.ikozlov.kanban.manager;

import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.util.List;

public interface TaskManager {
    public List<Task> getAllTasks();

    public void clearTasks();

    public Task getTask(int id);

    public Task createTask(Task task);

    public Task updateTask(int id, Task task);

    public Task deleteTask(int id);

    public List<Epic> getAllEpics();

    public void clearEpics();

    public Epic getEpic(int id);

    public Epic createEpic(Epic epic);

    public Epic updateEpic(int id, Epic epic);

    public Epic deleteEpic(int id);

    public List<Subtask> getEpicSubtasks(int id);

    public List<Subtask> getAllSubtasks();

    public void clearSubtasks();

    public Subtask getSubtask(int id);

    public Subtask createSubtask(Subtask subtask);

    public Subtask updateSubtask(int id, Subtask subtask);

    public Subtask deleteSubtask(int id);

    public List<Task> getHistory();

    public enum TaskType {
        TASK,
        SUBTASK,
        EPIC
    }
}
