package ru.ikozlov.kanban.manager.filebacked;

import ru.ikozlov.kanban.manager.TaskManager;
import ru.ikozlov.kanban.task.Task;

import java.io.File;

public interface FileStorage {
    void save(TaskManager taskManager, File file);

    FileBackedTaskManager load(File file);

    Task fromString(String string);
}
