package ru.ikozlov.kanban.testdata;

import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

public class SubtaskBuilder {
    private int id;
    private String title;
    private String description;
    private Task.Status status;
    private Epic epic;

    public SubtaskBuilder(int id, Epic epic) {
        this.id = id;
        this.epic = epic;
        this.title = String.format("Subtask %d", id);
        this.description = String.format("Subtask %d description", id);
        this.status = Task.Status.NEW;
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

    public Subtask build() {
        Subtask subtask = new Subtask(title, description, status, epic);
        subtask.setId(id);
        return subtask;
    }

}
