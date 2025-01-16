package ru.ikozlov.kanban.manager;

import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.util.HashMap;
import java.util.List;

public class ImMemoryTaskManager implements TaskManager {
    private int tasksCount = 0;
    private final HashMap<TaskType, HashMap<Integer, Task>> taskStorageByType;
    private final HistoryManager historyManager;

    public ImMemoryTaskManager() {
        taskStorageByType = new HashMap<>();
        taskStorageByType.put(TaskType.TASK, new HashMap<>());
        taskStorageByType.put(TaskType.SUBTASK, new HashMap<>());
        taskStorageByType.put(TaskType.EPIC, new HashMap<>());
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getAllTasks() {
        return taskStorageByType.get(TaskType.TASK).values().stream().toList();
    }

    @Override
    public void clearTasks() {
        taskStorageByType.put(TaskType.TASK, new HashMap<>());
    }

    @Override
    public Task getTask(int id) {
        Task task = taskStorageByType.get(TaskType.TASK).get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        tasksCount++;
        Task newTask = new Task(task.getTitle(), task.getDescription(), task.getStatus());
        newTask.setId(tasksCount);
        taskStorageByType.get(TaskType.TASK).put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Task updateTask(int id, Task task) {
        Task oldTask = taskStorageByType.get(TaskType.TASK).get(id);
        if (oldTask == null) {
            return null;
        }
        oldTask.setTitle(task.getTitle());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        return oldTask;
    }

    @Override
    public Task deleteTask(int id) {
        return taskStorageByType.get(TaskType.TASK).remove(id);
    }

    @Override
    public List<Epic> getAllEpics() {
        return taskStorageByType.get(TaskType.EPIC).values().stream()
                .map(x -> (Epic) x).toList();
    }

    @Override
    public void clearEpics() {
        taskStorageByType.put(TaskType.EPIC, new HashMap<>());
        clearSubtasks();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        tasksCount++;
        Epic newEpic = new Epic(epic.getTitle(), epic.getDescription(), epic.getSubtasks());
        newEpic.setId(tasksCount);
        newEpic.updateStatus();
        taskStorageByType.get(TaskType.EPIC).put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Epic updateEpic(int id, Epic epic) {
        Epic oldEpic = (Epic) taskStorageByType.get(TaskType.EPIC).get(id);
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

    @Override
    public Epic deleteEpic(int id) {
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).remove(id);
        epic.getSubtasks().forEach(x -> taskStorageByType.get(TaskType.SUBTASK).remove(x.getId()));
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(id);
        return epic == null ? null : epic.getSubtasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return taskStorageByType.get(TaskType.SUBTASK).values().stream()
                .map(x -> (Subtask) x).toList();
    }

    @Override
    public void clearSubtasks() {
        taskStorageByType.put(TaskType.SUBTASK, new HashMap<>());
        getAllEpics().forEach(Epic::updateStatus);
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = (Subtask) taskStorageByType.get(TaskType.SUBTASK).get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        tasksCount++;
        int epicId = subtask.getEpic().getId();
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(epicId);
        Subtask newSubtask = new Subtask(subtask.getTitle(), subtask.getDescription(), subtask.getStatus(), epic);
        newSubtask.setId(tasksCount);
        taskStorageByType.get(TaskType.SUBTASK).put(newSubtask.getId(), newSubtask);
        return newSubtask;
    }

    @Override
    public Subtask updateSubtask(int id, Subtask subtask) {
        Subtask oldSubtask = (Subtask) taskStorageByType.get(TaskType.SUBTASK).get(id);
        if (oldSubtask == null) {
            return null;
        }
        oldSubtask.setTitle(subtask.getTitle());
        oldSubtask.setDescription(subtask.getDescription());
        oldSubtask.setStatus(subtask.getStatus());
        oldSubtask.setEpic(subtask.getEpic());
        int epicId = oldSubtask.getEpic().getId();
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(epicId);
        epic.updateStatus();
        return oldSubtask;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask subtask = (Subtask) taskStorageByType.get(TaskType.SUBTASK).remove(id);
        if (subtask == null) {
            return null;
        }
        int epicId = subtask.getEpic().getId();
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(epicId);
        epic.setSubtasks(epic.getSubtasks().stream().filter(x -> x != subtask).toList());
        epic.updateStatus();
        return subtask;
    }


    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
