package ru.ikozlov.kanban.manager;

import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private int tasksCount = 0;
    private final HashMap<TaskType, HashMap<Integer, Task>> taskStorageByType;

    public TaskManager() {
        taskStorageByType = new HashMap<>();
        taskStorageByType.put(TaskType.TASK, new HashMap<>());
        taskStorageByType.put(TaskType.SUBTASK, new HashMap<>());
        taskStorageByType.put(TaskType.EPIC, new HashMap<>());
    }

    public List<Task> getAllTasks() {
        return taskStorageByType.get(TaskType.TASK).values().stream().toList();
    }

    public void clearTasks() {
        taskStorageByType.put(TaskType.TASK, new HashMap<>());
    }

    public Task getTask(int id) {
        return taskStorageByType.get(TaskType.TASK).get(id);
    }

    public Task createTask(Task task) {
        tasksCount++;
        task.setId(tasksCount);
        taskStorageByType.get(TaskType.TASK).put(task.getId(), task);
        return task;
    }

    public Task updateTask(int id, Task task) {
        Task oldTask = getTask(id);
        if (oldTask == null) {
            return null;
        }
        oldTask.setTitle(task.getTitle());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        return oldTask;
    }

    public Task deleteTask(int id) {
        return taskStorageByType.get(TaskType.TASK).remove(id);
    }

    public List<Epic> getAllEpics() {
        return taskStorageByType.get(TaskType.EPIC).values().stream()
                .map(x -> (Epic) x).toList();
    }

    public void clearEpics() {
        taskStorageByType.put(TaskType.EPIC, new HashMap<>());
        clearSubtasks();
    }

    public Epic getEpic(int id) {
        return (Epic) taskStorageByType.get(TaskType.EPIC).get(id);
    }

    public Epic createEpic(Epic epic) {
        tasksCount++;
        epic.setId(tasksCount);
        epic.updateStatus();
        taskStorageByType.get(TaskType.EPIC).put(epic.getId(), epic);
        return epic;
    }

    public Epic updateEpic(int id, Epic epic) {
        Epic oldEpic = getEpic(id);
        if (oldEpic == null) {
            return null;
        }
        oldEpic.setTitle(epic.getTitle());
        oldEpic.setDescription(epic.getDescription());
        oldEpic.setStatus(epic.getStatus());
        oldEpic.setSubtasks(epic.getSubtasks());
        oldEpic.updateStatus();
        return oldEpic;
    }

    public Epic deleteEpic(int id) {
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).remove(id);
        epic.getSubtasks().forEach(x -> taskStorageByType.get(TaskType.SUBTASK).remove(x.getId()));
        return epic;
    }

    public List<Subtask> getEpicSubtasks(int id) {
        Epic epic = getEpic(id);
        return epic == null ? null : epic.getSubtasks();
    }

    public List<Subtask> getAllSubtasks() {
        return taskStorageByType.get(TaskType.SUBTASK).values().stream()
                .map(x -> (Subtask) x).toList();
    }

    public void clearSubtasks() {
        taskStorageByType.put(TaskType.SUBTASK, new HashMap<>());
        getAllEpics().forEach(Epic::updateStatus);
    }

    public Subtask getSubtask(int id) {
        return (Subtask) taskStorageByType.get(TaskType.SUBTASK).get(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        tasksCount++;
        subtask.setId(tasksCount);
        taskStorageByType.get(TaskType.SUBTASK).put(subtask.getId(), subtask);
        return subtask;
    }

    public Subtask updateSubtask(int id, Subtask subtask) {
        Subtask oldSubtask = getSubtask(id);
        if (oldSubtask == null) {
            return null;
        }
        oldSubtask.setTitle(subtask.getTitle());
        oldSubtask.setDescription(subtask.getDescription());
        oldSubtask.setStatus(subtask.getStatus());
        oldSubtask.setEpic(subtask.getEpic());
        getEpic(oldSubtask.getEpic().getId()).updateStatus();
        return oldSubtask;
    }

    public Subtask deleteSubtask(int id) {
        Subtask subtask = (Subtask) taskStorageByType.get(TaskType.SUBTASK).remove(id);
        if (subtask == null) {
            return null;
        }
        Epic epic = getEpic(subtask.getEpic().getId());
        epic.setSubtasks(epic.getSubtasks().stream().filter(x -> x != subtask).toList());
        epic.updateStatus();
        return subtask;
    }

    public enum TaskType {
        TASK,
        SUBTASK,
        EPIC
    }

}
