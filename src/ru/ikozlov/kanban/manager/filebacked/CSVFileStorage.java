package ru.ikozlov.kanban.manager.filebacked;

import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.manager.TaskType;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;

public class CSVFileStorage implements FileStorage {

    public void save(TaskManager taskManager, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            for (Task task : taskManager.getAllTasks()) {
                String line = String.format("%d,%s,%s,%s,%s,", task.getId(), TaskType.TASK, task.getTitle(),
                        task.getStatus(), task.getDescription());
                writer.write(line);
                writer.newLine();
            }
            for (Epic epic : taskManager.getAllEpics()) {
                String line = String.format("%d,%s,%s,%s,%s,", epic.getId(), TaskType.EPIC, epic.getTitle(),
                        epic.getStatus(), epic.getDescription());
                writer.write(line);
                writer.newLine();
            }
            for (Subtask subtask : taskManager.getAllSubtasks()) {
                String line = String.format("%d,%s,%s,%s,%s,%d", subtask.getId(), TaskType.SUBTASK, subtask.getTitle(),
                        subtask.getStatus(), subtask.getDescription(), subtask.getEpic().getId());
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    public FileBackedTaskManager load(File file) {
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
        Set<Task> loadedTasks = new TreeSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                Task task = fromString(reader.readLine());
                loadedTasks.add(task);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        for (Task task : loadedTasks) {
            if (task instanceof Epic) {
                taskManager.createEpic((Epic) task);
            } else if (task instanceof Subtask) {
                taskManager.createSubtask((Subtask) task);
            } else {
                taskManager.createTask(task);
            }
        }
        return taskManager;
    }

    public Task fromString(String string) {
        String[] data = string.split(",");
        int id = Integer.parseInt(data[0]);
        TaskType type = TaskType.valueOf(data[1]);
        String title = data[2];
        Task.Status status = Task.Status.valueOf(data[3]);
        String description = data[4];

        return switch (type) {
            case TASK -> {
                Task task = new Task(title, description, status);
                task.setId(id);
                yield task;
            }
            case EPIC -> {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                yield epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(data[5]);
                Epic epic = new Epic("temp", "temp");
                epic.setId(epicId);
                Subtask subtask = new Subtask(title, description, status, epic);
                subtask.setId(id);
                yield subtask;
            }
        };
    }
}
