package ru.ikozlov.kanban.testdata;

import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.util.ArrayList;
import java.util.List;

public class EpicBuilder {
    private int id;
    private String title;
    private String description;
    private Task.Status status;
    private List<Subtask> subtasks;

    public EpicBuilder(int id) {
        this.id = id;
        this.title = String.format("Epic %d", id);
        this.description = String.format("Epic %d description", id);
        this.status = Task.Status.NEW;
        this.subtasks = new ArrayList<>();
    }

    public EpicBuilder title(String title) {
        this.title = title;
        return this;
    }

    public EpicBuilder description(String description) {
        this.description = description;
        return this;
    }

    public EpicBuilder status(Task.Status status) {
        this.status = status;
        return this;
    }

    public EpicBuilder subtasks(List<Subtask> subtasks) {
        this.subtasks = subtasks;
        return this;
    }

    public Epic build() {
        Epic epic = new Epic(title, description, subtasks);
        epic.setId(id);
        return epic;
    }

}
