package ru.ikozlov.kanban.testdata;

import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public class SubtaskBuilder {
    private int id;
    private String title;
    private String description;
    private Task.Status status;
    private Epic epic;
    private Duration duration;
    private LocalDateTime startTime;

    public SubtaskBuilder(int id, Epic epic) {
        this.id = id;
        this.epic = epic;
        this.title = String.format("Subtask %d", id);
        this.description = String.format("Subtask %d description", id);
        this.status = Task.Status.NEW;
        Random random = new Random();
        this.duration = Duration.ofMinutes(random.nextInt(30, 180));
        this.startTime = LocalDateTime.now().plusMinutes(random.nextInt(360));
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
        Subtask subtask = new Subtask(title, description, status, epic, duration, startTime);
        subtask.setId(id);
        return subtask;
    }

}
