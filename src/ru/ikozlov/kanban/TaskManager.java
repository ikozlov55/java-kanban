package ru.ikozlov.kanban;

import java.util.Arrays;
import java.util.HashMap;

public class TaskManager {
    private static int tasksCount = 0;
    private final HashMap<TaskType, HashMap<Integer, Task>> map;

    public TaskManager() {
        map = new HashMap<>();
        map.put(TaskType.TASK, new HashMap<>());
        map.put(TaskType.SUBTASK, new HashMap<>());
        map.put(TaskType.EPIC, new HashMap<>());
    }

    public Task[] getAllTasks() {
        return map.get(TaskType.TASK).values().toArray(new Task[0]);
    }

    public void clearTasks() {
        map.put(TaskType.TASK, new HashMap<>());
    }

    public Task getTask(int id) {
        return map.get(TaskType.TASK).getOrDefault(id, null);
    }

    public Task createTask(Task task) {
        tasksCount++;
        task.setId(tasksCount);
        map.get(TaskType.TASK).put(task.getId(), task);
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
        return map.get(TaskType.TASK).remove(id);
    }

    public Epic[] getAllEpics() {
        return map.get(TaskType.EPIC).values().toArray(new Epic[0]);
    }

    public void clearEpics() {
        map.put(TaskType.EPIC, new HashMap<>());
    }

    public Epic getEpic(int id) {
        return (Epic) map.get(TaskType.EPIC).getOrDefault(id, null);
    }

    public Epic createEpic(Epic epic) {
        tasksCount++;
        epic.setId(tasksCount);
        epic.updateStatus();
        map.get(TaskType.EPIC).put(epic.getId(), epic);
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
        return (Epic) map.get(TaskType.EPIC).remove(id);
    }

    public Subtask[] getEpicSubtasks(int id) {
        Epic epic = (Epic) map.get(TaskType.EPIC).get(id);
        if (epic == null) {
            return null;
        }
        return epic.getSubtasks();
    }

    public Subtask[] getAllSubtasks() {
        return map.get(TaskType.SUBTASK).values().toArray(new Subtask[0]);
    }

    public void clearSubtasks() {
        map.put(TaskType.SUBTASK, new HashMap<>());
    }

    public Subtask getSubtask(int id) {
        return (Subtask) map.get(TaskType.SUBTASK).getOrDefault(id, null);
    }

    public Subtask createSubtask(Subtask subtask) {
        tasksCount++;
        subtask.setId(tasksCount);
        map.get(TaskType.SUBTASK).put(subtask.getId(), subtask);
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
        oldSubtask.getEpic().updateStatus();
        return subtask;
    }

    public Subtask deleteSubtask(int id) {
        Subtask subtask = (Subtask) map.get(TaskType.SUBTASK).get(id);
        if (subtask == null) {
            return null;
        }
        Epic epic = subtask.getEpic();
        epic.setSubtasks(Arrays.stream(epic.getSubtasks()).filter(x -> x != subtask).toArray(Subtask[]::new));
        epic.updateStatus();
        return subtask;
    }

    public enum TaskType {
        TASK,
        SUBTASK,
        EPIC
    }

}
