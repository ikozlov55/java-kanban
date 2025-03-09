package ru.ikozlov.kanban.testdata;

import ru.ikozlov.kanban.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskBuilder {
    protected final int id;
    protected String title;
    protected String description;
    protected Task.Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public TaskBuilder(int id) {
        this.id = id;
        this.title = String.format("Task %d", id);
        this.description = String.format("Task %d description", id);
        this.status = Task.Status.NEW;
        this.duration = Duration.ZERO;
        this.startTime = LocalDateTime.now().plusHours(id);
    }

    public TaskBuilder title(String title) {
        this.title = title;
        return this;
    }

    public TaskBuilder description(String description) {
        this.description = description;
        return this;
    }

    public TaskBuilder status(Task.Status status) {
        this.status = status;
        return this;
    }

    public TaskBuilder duration(Duration duration) {
        this.duration = duration;
        return this;
    }

    public TaskBuilder startTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public Task build() {
        Task task = new Task(title, description, status, duration, startTime);
        task.setId(id);
        return task;
    }

}
