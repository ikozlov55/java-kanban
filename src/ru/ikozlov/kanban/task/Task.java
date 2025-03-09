package ru.ikozlov.kanban.task;

import ru.ikozlov.kanban.manager.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected Integer id;
    protected String title;
    protected String description;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String title, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id == null) {
            this.id = id;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id.equals(task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s, ,%s,%s",
                id, TaskType.TASK, title, status, description, duration, startTime);
    }

    @Override
    public int compareTo(Task o) {
        return id - o.getId();
    }

    public Task copy() {
        Task copy = new Task(title, description, status, duration, startTime);
        copy.setId(getId());
        return copy;
    }

    public boolean intersectsWith(Task other) {
        if (id != null && other.id != null && equals(other)) {
            return false;
        }
        if (startTime == null || other.startTime == null) {
            return false;
        }
        return dateBetween(startTime, other.startTime, other.getEndTime()) ||
                dateBetween(getEndTime(), other.startTime, other.getEndTime()) ||
                dateBetween(other.startTime, startTime, getEndTime()) ||
                dateBetween(other.getEndTime(), startTime, getEndTime());
    }

    private boolean dateBetween(LocalDateTime d, LocalDateTime start, LocalDateTime end) {
        return d.isEqual(start) || d.isAfter(start) && d.isBefore(end);
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }
}
