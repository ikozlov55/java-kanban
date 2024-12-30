package ru.ikozlov.kanban.task;

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
        List<Integer> subtaskIds = getSubtasks().stream()
                .mapToInt(Subtask::getId).boxed()
                .toList();
        return String.format("Epic #%d\nStatus: %s\nTitle: %s\n%s\nSubtasks: %s\n", getId(),
                getStatus(), getTitle(), getDescription(), subtaskIds);
    }


}
