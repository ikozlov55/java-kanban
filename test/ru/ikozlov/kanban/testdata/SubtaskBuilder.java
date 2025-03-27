package ru.ikozlov.kanban.testdata;

import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskBuilder {
    private final Integer id;
    private String title;
    private String description;
    private Task.Status status;
    private Integer epicId;
    private Duration duration;
    private LocalDateTime startTime;

    public SubtaskBuilder(Integer id, Integer epicId) {
        this.id = id;
        this.epicId = epicId;
        this.title = String.format("Subtask %d", id);
        this.description = String.format("Subtask %d description", id);
        this.status = Task.Status.NEW;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now().plusHours(id != null ? id : 0);
    }

    public SubtaskBuilder title(String title) {
        this.title = title;
        return this;
    }

    public SubtaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    public SubtaskBuilder status(Task.Status status) {
        this.status = status;
        return this;
    }

    public SubtaskBuilder duration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public SubtaskBuilder startTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public Subtask build() {
        Subtask subtask = new Subtask(title, description, status, epicId, duration, startTime);
        if (id != null) {
            subtask.setId(id);
        }
        return subtask;
    }

}
