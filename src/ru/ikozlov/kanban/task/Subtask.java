package ru.ikozlov.kanban.task;

import ru.ikozlov.kanban.manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String title, String description, Status status, int epicId, Duration duration,
                   LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s", id, TaskType.SUBTASK, title,
                status, description, epicId, duration, startTime);
    }

    public Subtask copy() {
        Subtask copy = new Subtask(title, description, status, epicId, duration, startTime);
        copy.setId(getId());
        return copy;
    }
}
