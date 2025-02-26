package ru.ikozlov.kanban.task;

import ru.ikozlov.kanban.manager.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        this.subtasks = new ArrayList<>();
    }

    public Epic(String title, String description, List<Subtask> subtasks) {
        super(title, description, Status.NEW);
        setSubtasks(subtasks);
        updateStatus();
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = new TreeSet<>(subtasks).stream().toList();
        updateStatus();
    }

    public void updateStatus() {
        int newSubtasks = 0;
        int doneSubtasks = 0;
        for (Subtask subtask : subtasks) {
            switch (subtask.getStatus()) {
                case NEW:
                    newSubtasks++;
                    break;
                case DONE:
                    doneSubtasks++;
                    break;
            }
        }
        Task.Status newStatus = Task.Status.IN_PROGRESS;
        if (subtasks.isEmpty() || newSubtasks == subtasks.size()) {
            newStatus = Task.Status.NEW;
        } else if (doneSubtasks == subtasks.size()) {
            newStatus = Task.Status.DONE;
        }
        setStatus(newStatus);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,", getId(), TaskType.EPIC, getTitle(), getStatus(), getDescription());
    }

    public Epic copy() {
        Epic copy = new Epic(getTitle(), getDescription());
        copy.setId(getId());
        copy.setSubtasks(getSubtasks());
        return copy;
    }

}
