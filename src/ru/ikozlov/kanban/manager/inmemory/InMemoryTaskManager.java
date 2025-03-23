package ru.ikozlov.kanban.manager.inmemory;

import ru.ikozlov.kanban.manager.*;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int tasksCount = 0;
    protected final HashMap<TaskType, HashMap<Integer, Task>> taskStorageByType;
    protected final HistoryManager historyManager;
    protected final TreeSet<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        taskStorageByType = new HashMap<>();
        taskStorageByType.put(TaskType.TASK, new HashMap<>());
        taskStorageByType.put(TaskType.SUBTASK, new HashMap<>());
        taskStorageByType.put(TaskType.EPIC, new HashMap<>());
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected void updatePriority(Task task) {
        prioritizedTasks.removeIf(x -> x.getId().equals(task.getId()));
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private boolean intersectsWithOtherTasks(Task task) {
        return getPrioritizedTasks().stream().anyMatch(task::intersectsWith);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskStorageByType.get(TaskType.TASK).values().stream().toList();
    }

    @Override
    public void clearTasks() {
        Set<Integer> taskIds = taskStorageByType.get(TaskType.TASK).keySet();
        taskIds.forEach(historyManager::remove);
        prioritizedTasks.removeIf(x -> taskIds.contains(x.getId()));
        taskStorageByType.put(TaskType.TASK, new HashMap<>());
    }

    @Override
    public Task getTask(int id) {
        Task task = taskStorageByType.get(TaskType.TASK).get(id);
        if (task == null) {
            throw new NotFoundException();
        }
        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        if (intersectsWithOtherTasks(task)) {
            throw new TimeIntersectionException();
        }
        tasksCount++;
        Task newTask = new Task(task.getTitle(), task.getDescription(), task.getStatus(), task.getDuration(),
                task.getStartTime());
        newTask.setId(tasksCount);
        updatePriority(newTask);
        taskStorageByType.get(TaskType.TASK).put(newTask.getId(), newTask);
        return newTask;
    }

    @Override
    public Task updateTask(int id, Task task) {
        Task oldTask = taskStorageByType.get(TaskType.TASK).get(id);
        if (oldTask == null) {
            throw new NotFoundException();
        }
        if (intersectsWithOtherTasks(task)) {
            throw new TimeIntersectionException();
        }
        oldTask.setTitle(task.getTitle());
        oldTask.setDescription(task.getDescription());
        oldTask.setStatus(task.getStatus());
        oldTask.setDuration(task.getDuration());
        oldTask.setStartTime(task.getStartTime());
        updatePriority(oldTask);
        return oldTask;
    }

    @Override
    public Task deleteTask(int id) {
        Task task = taskStorageByType.get(TaskType.TASK).remove(id);
        if (task == null) {
            return null;
        }
        historyManager.remove(task.getId());
        prioritizedTasks.remove(task);
        return task;
    }

    @Override
    public List<Epic> getAllEpics() {
        return taskStorageByType.get(TaskType.EPIC).values().stream()
                .map(x -> (Epic) x).toList();
    }

    @Override
    public void clearEpics() {
        taskStorageByType.get(TaskType.EPIC).keySet().forEach(historyManager::remove);
        taskStorageByType.put(TaskType.EPIC, new HashMap<>());
        clearSubtasks();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(id);
        if (epic == null) {
            throw new NotFoundException();
        }
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        tasksCount++;
        Epic newEpic = new Epic(epic.getTitle(), epic.getDescription());
        newEpic.setId(tasksCount);
        taskStorageByType.get(TaskType.EPIC).put(newEpic.getId(), newEpic);
        return newEpic;
    }

    @Override
    public Epic updateEpic(int id, Epic epic) {
        Epic oldEpic = (Epic) taskStorageByType.get(TaskType.EPIC).get(id);
        if (oldEpic == null) {
            throw new NotFoundException();
        }
        oldEpic.setTitle(epic.getTitle());
        oldEpic.setDescription(epic.getDescription());
        oldEpic.setSubtasks(epic.getSubtasks());
        return oldEpic;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).remove(id);
        if (epic == null) {
            return null;
        }
        historyManager.remove(epic.getId());
        epic.getSubtasks().forEach(x -> {
            Task subtask = taskStorageByType.get(TaskType.SUBTASK).remove(x.getId());
            historyManager.remove(subtask.getId());
        });
        return epic;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int id) {
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(id);
        if (epic == null) {
            throw new NotFoundException();
        }
        return epic.getSubtasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return taskStorageByType.get(TaskType.SUBTASK).values().stream()
                .map(x -> (Subtask) x).toList();
    }

    @Override
    public void clearSubtasks() {
        Set<Integer> taskIds = taskStorageByType.get(TaskType.SUBTASK).keySet();
        taskIds.forEach(historyManager::remove);
        taskStorageByType.put(TaskType.SUBTASK, new HashMap<>());
        prioritizedTasks.removeIf(x -> taskIds.contains(x.getId()));
        getAllEpics().forEach(e -> e.setSubtasks(new ArrayList<>()));
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = (Subtask) taskStorageByType.get(TaskType.SUBTASK).get(id);
        if (subtask == null) {
            throw new NotFoundException();
        }
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (intersectsWithOtherTasks(subtask)) {
            throw new TimeIntersectionException();
        }
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(subtask.getEpicId());
        if (epic == null) {
            throw new NotFoundException();
        }
        tasksCount++;
        Subtask newSubtask = new Subtask(subtask.getTitle(), subtask.getDescription(), subtask.getStatus(),
                subtask.getEpicId(), subtask.getDuration(), subtask.getStartTime());
        newSubtask.setId(tasksCount);
        updatePriority(newSubtask);
        List<Subtask> subtasks = epic.getSubtasks();
        subtasks.add(newSubtask);
        epic.setSubtasks(subtasks);
        taskStorageByType.get(TaskType.SUBTASK).put(newSubtask.getId(), newSubtask);
        return newSubtask;
    }

    @Override
    public Subtask updateSubtask(int id, Subtask subtask) {
        Subtask oldSubtask = (Subtask) taskStorageByType.get(TaskType.SUBTASK).get(id);
        if (oldSubtask == null) {
            throw new NotFoundException();
        }
        if (intersectsWithOtherTasks(subtask)) {
            throw new TimeIntersectionException();
        }
        oldSubtask.setTitle(subtask.getTitle());
        oldSubtask.setDescription(subtask.getDescription());
        oldSubtask.setStatus(subtask.getStatus());
        oldSubtask.setEpicId(subtask.getEpicId());
        oldSubtask.setDuration(subtask.getDuration());
        oldSubtask.setStartTime(subtask.getStartTime());
        updatePriority(oldSubtask);
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(oldSubtask.getEpicId());
        epic.updateStatus();
        return oldSubtask;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask subtask = (Subtask) taskStorageByType.get(TaskType.SUBTASK).remove(id);
        if (subtask == null) {
            return null;
        }
        historyManager.remove(subtask.getId());
        prioritizedTasks.remove(subtask);
        Epic epic = (Epic) taskStorageByType.get(TaskType.EPIC).get(subtask.getEpicId());
        epic.setSubtasks(epic.getSubtasks().stream().filter(x -> x != subtask).toList());
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
