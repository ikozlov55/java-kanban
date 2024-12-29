package ru.ikozlov.kanban;

import java.util.Arrays;
import java.util.TreeSet;

public class Epic extends Task {
    private Subtask[] subtasks;

    public Epic(String title, String description) {
        super(title, description, Status.NEW);
        this.subtasks = new Subtask[0];
    }

    public Epic(String title, String description, Subtask[] subtasks) {
        super(title, description, Status.NEW);
        setSubtasks(subtasks);
        updateStatus();
    }

    public Subtask[] getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(Subtask[] subtasks) {
        this.subtasks = new TreeSet<>(Arrays.stream(subtasks).toList()).toArray(new Subtask[0]);
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
        if (subtasks.length == 0 || newSubtasks == subtasks.length) {
            newStatus = Task.Status.NEW;
        } else if (doneSubtasks == subtasks.length) {
            newStatus = Task.Status.DONE;
        }
        setStatus(newStatus);
    }

    @Override
    public String toString() {
        int[] subtaskIds = Arrays.stream(getSubtasks()).mapToInt(Subtask::getId).toArray();
        return String.format("Epic #%d\nStatus: %s\nTitle: %s\n%s\nSubtasks: %s\n", getId(),
                getStatus(), getTitle(), getDescription(), Arrays.toString(subtaskIds));
    }


}
