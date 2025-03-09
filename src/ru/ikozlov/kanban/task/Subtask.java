package ru.ikozlov.kanban.task;

import ru.ikozlov.kanban.manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, Status status, Epic epic, Duration duration,
                   LocalDateTime startTime) {
        super(title, description, status, duration, startTime);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic.copy();
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s", id, TaskType.SUBTASK, title,
                status, description, epic.id, duration, startTime);
    }

    public Subtask copy() {
        Subtask copy = new Subtask(title, description, status, getEpic(), duration, startTime);
        copy.setId(getId());
        return copy;
    }
}
