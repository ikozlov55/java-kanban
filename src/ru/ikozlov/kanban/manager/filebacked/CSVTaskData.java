package ru.ikozlov.kanban.manager.filebacked;

import ru.ikozlov.kanban.manager.TaskType;
import ru.ikozlov.kanban.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class CSVTaskData {
    protected final int id;
    protected final TaskType type;
    protected final String title;
    protected final Task.Status status;
    protected final String description;
    protected final int epicId;
    protected final Duration duration;
    protected final LocalDateTime startTime;

    public CSVTaskData(String line) {
        String[] data = line.split(",");
        this.id = Integer.parseInt(data[0]);
        this.type = TaskType.valueOf(data[1]);
        this.title = data[2];
        this.status = Task.Status.valueOf(data[3]);
        this.description = data[4];
        this.epicId = parseEpicId(data[5]);
        this.duration = Duration.parse(data[6]);
        this.startTime = parseStartTime(data[7]);
    }

    private int parseEpicId(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private LocalDateTime parseStartTime(String str) {
        try {
            return LocalDateTime.parse(str);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}