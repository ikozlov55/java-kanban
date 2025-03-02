package ru.ikozlov.kanban.manager.filebacked;

import ru.ikozlov.kanban.manager.TaskType;
import ru.ikozlov.kanban.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public class CSVTaskData {
    final public int id;
    final public TaskType type;
    final public String title;
    final public Task.Status status;
    final public String description;
    final public int epicId;
    final public Duration duration;
    final public LocalDateTime startTime;

    public CSVTaskData(String line) {
        String[] data = line.split(",");
        int epicId;
        LocalDateTime startTime;
        this.id = Integer.parseInt(data[0]);
        this.type = TaskType.valueOf(data[1]);
        this.title = data[2];
        this.status = Task.Status.valueOf(data[3]);
        this.description = data[4];
        try {
            epicId = Integer.parseInt(data[5]);
        } catch (NumberFormatException e) {
            epicId = 0;
        }
        this.epicId = epicId;
        this.duration = Duration.parse(data[6]);
        try {
            startTime = LocalDateTime.parse(data[7]);
        } catch (DateTimeParseException e) {
            System.out.println(e.getMessage());
            startTime = null;
        }
        this.startTime = startTime;
    }
}