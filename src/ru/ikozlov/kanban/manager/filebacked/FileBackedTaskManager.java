package ru.ikozlov.kanban.manager.filebacked;

import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.manager.inmemory.InMemoryTaskManager;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final FileStorage fileStorage = new CSVFileStorage();

    public FileBackedTaskManager(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new ManagerSaveException(e.getMessage());
            }
        }
        this.file = file;
    }

    public static void main(String[] args) {
        File file = new File("taskmanager.csv");
        TaskManager manager = new FileBackedTaskManager(file);
        manager.createTask(new Task("Task1", "Task1", Task.Status.NEW));
        manager.createTask(new Task("Task2", "Task2", Task.Status.IN_PROGRESS));
        Epic epic1 = manager.createEpic(new Epic("Epic1", "Epic1"));
        manager.createSubtask(new Subtask("Subtask1", "Subtask1", Task.Status.NEW, epic1));
        manager.createSubtask(new Subtask("Subtask2", "Subtask2", Task.Status.IN_PROGRESS, epic1));
        manager.createSubtask(new Subtask("Subtask3", "Subtask3", Task.Status.DONE, epic1));
        manager.createEpic(new Epic("Epic2", "Epic2"));

        TaskManager managerLoaded = FileBackedTaskManager.loadFromFile(file);

        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
            System.out.println(managerLoaded.getTask(task.getId()));
        }

        for (Epic epic : manager.getAllEpics()) {
            System.out.println(epic);
            System.out.println(managerLoaded.getEpic(epic.getId()));
        }

        for (Subtask subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
            System.out.println(managerLoaded.getSubtask(subtask.getId()));
        }
    }

    private void save() {
        fileStorage.save(this, file);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return fileStorage.load(file);
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task newTask = super.createTask(task);
        save();
        return newTask;
    }

    @Override
    public Task updateTask(int id, Task task) {
        Task oldTask = super.updateTask(id, task);
        if (oldTask != null) {
            save();
        }
        return oldTask;
    }

    @Override
    public Task deleteTask(int id) {
        Task task = super.deleteTask(id);
        if (task != null) {
            save();
        }
        return task;
    }


    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }


    @Override
    public Epic createEpic(Epic epic) {
        Epic newEpic = super.createEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Epic updateEpic(int id, Epic epic) {
        Epic oldEpic = super.updateEpic(id, epic);
        if (oldEpic != null) {
            save();
        }
        return oldEpic;
    }

    @Override
    public Epic deleteEpic(int id) {
        Epic epic = super.deleteEpic(id);
        if (epic == null) {
            save();
        }
        return epic;
    }


    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask newSubtask = super.createSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Subtask updateSubtask(int id, Subtask subtask) {
        Subtask oldSubtask = super.updateSubtask(id, subtask);
        if (oldSubtask == null) {
            save();
        }
        return oldSubtask;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask subtask = super.deleteSubtask(id);
        if (subtask == null) {
            save();
        }
        return subtask;
    }

}
