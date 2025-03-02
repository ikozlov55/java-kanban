package ru.ikozlov.kanban.task;

import ru.ikozlov.kanban.manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Epic extends Task {
    private List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description, Status.NEW, Duration.ZERO, null);
        this.endTime = null;
        this.subtasks = new ArrayList<>();
    }

    public Epic(String title, String description, List<Subtask> subtasks) {
        super(title, description, Status.NEW, Duration.ZERO, null);
        setSubtasks(subtasks);
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public void setSubtasks(List<Subtask> subtasks) {
        this.subtasks = new TreeSet<>(subtasks).stream().toList();
        updateStatus();
        updateMetrics();
    }

    @Override
    public void setStatus(Status status) {
    }

    @Override
    public void setDuration(Duration duration) {
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void updateStatus() {
        int newSubtasks = 0;
        int doneSubtasks = 0;
        for (Subtask subtask : subtasks) {
            switch (subtask.status) {
                case NEW -> newSubtasks++;
                case DONE -> doneSubtasks++;
            }
        }
        if (subtasks.isEmpty() || newSubtasks == subtasks.size()) {
            status = Task.Status.NEW;
        } else if (doneSubtasks == subtasks.size()) {
            status = Task.Status.DONE;
        } else {
            status = Task.Status.IN_PROGRESS;
        }
    }

    public void updateMetrics() {
        duration = Duration.ZERO;
        LocalDateTime minStartTime = null;
        LocalDateTime maxEndTime = null;
        for (Subtask subtask : subtasks) {
            duration = duration.plus(subtask.duration);
            if (minStartTime == null) {
                minStartTime = subtask.startTime;
            } else if (subtask.startTime != null && subtask.startTime.isBefore(minStartTime)) {
                minStartTime = subtask.startTime;
            }
            if (maxEndTime == null) {
                maxEndTime = subtask.getEndTime();
            } else if (subtask.getEndTime() != null && subtask.getEndTime().isAfter(maxEndTime)) {
                maxEndTime = subtask.getEndTime();
            }
        }
        startTime = minStartTime;
        endTime = maxEndTime;
    }


    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s, ,%s,%s", id, TaskType.EPIC, title, status, description, duration,
                startTime);
    }

    public Epic copy() {
        Epic copy = new Epic(title, description);
        copy.setId(id);
        copy.setSubtasks(getSubtasks());
        return copy;
    }

}
