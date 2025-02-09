package ru.ikozlov.kanban.testdata;

import ru.ikozlov.kanban.task.Task;

public class TaskBuilder {
    private int id;
    private String title;
    private String description;
    private Task.Status status;

    public TaskBuilder(int id) {
        this.id = id;
        this.title = String.format("Task %d", id);
        this.description = String.format("Task %d description", id);
        this.status = Task.Status.NEW;
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

    public Task build() {
        Task task = new Task(title, description, status);
        task.setId(id);
        return task;
    }

}
