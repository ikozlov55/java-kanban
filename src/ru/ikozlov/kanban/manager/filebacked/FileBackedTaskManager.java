package ru.ikozlov.kanban.manager.filebacked;

import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.manager.TaskType;
import ru.ikozlov.kanban.manager.inmemory.InMemoryTaskManager;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public static final String HEADER = "id,type,name,status,description,epic,duration,startTime";
    private final File file;

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
        manager.createTask(new Task("Task1", "Task1", Task.Status.NEW, Duration.ofHours(1),
                LocalDateTime.now()));
        manager.createTask(new Task("Task2", "Task2", Task.Status.IN_PROGRESS, Duration.ofHours(2),
                LocalDateTime.now().minusHours(2)));
        Epic epic1 = manager.createEpic(new Epic("Epic1", "Epic1"));
        manager.createSubtask(new Subtask("Subtask1", "Subtask1", Task.Status.NEW, epic1,
                Duration.ofMinutes(30), LocalDateTime.now().plusHours(1)));
        manager.createSubtask(new Subtask("Subtask2", "Subtask2", Task.Status.IN_PROGRESS, epic1,
                Duration.ofMinutes(90), LocalDateTime.now().plusHours(2)));
        manager.createSubtask(new Subtask("Subtask3", "Subtask3", Task.Status.DONE, epic1,
                Duration.ofMinutes(121), LocalDateTime.now().plusHours(5)));
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(HEADER);
            writer.newLine();
            for (Task task : getAllTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString());
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(subtask.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                CSVTaskData data = new CSVTaskData(reader.readLine());
                switch (data.type) {
                    case TASK -> {
                        Task task = new Task(data.title, data.description, data.status, data.duration, data.startTime);
                        task.setId(data.id);
                        taskManager.taskStorageByType.get(TaskType.TASK).put(data.id, task);
                    }
                    case EPIC -> {
                        Epic epic = new Epic(data.title, data.description);
                        epic.setId(data.id);
                        taskManager.taskStorageByType.get(TaskType.EPIC).put(data.id, epic);
                    }
                    case SUBTASK -> {
                        Epic epic = (Epic) taskManager.taskStorageByType.get(TaskType.EPIC).get(data.epicId);
                        Subtask subtask = new Subtask(data.title, data.description, data.status, epic, data.duration,
                                data.startTime);
                        subtask.setId(data.id);
                        List<Subtask> subtasks = epic.getSubtasks();
                        subtasks.add(subtask);
                        epic.setSubtasks(subtasks);
                        taskManager.taskStorageByType.get(TaskType.SUBTASK).put(data.id, subtask);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
        return taskManager;
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
        if (epic != null) {
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
        if (oldSubtask != null) {
            save();
        }
        return oldSubtask;
    }

    @Override
    public Subtask deleteSubtask(int id) {
        Subtask subtask = super.deleteSubtask(id);
        if (subtask != null) {
            save();
        }
        return subtask;
    }

}
