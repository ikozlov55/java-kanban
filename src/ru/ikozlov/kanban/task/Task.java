package ru.ikozlov.kanban.task;

import java.util.Objects;

public class Task implements Comparable<Task> {
    private Integer id;
    private String title;
    private String description;
    private Status status;

    public Task(String title, String description, Status status) {
        this.id = null;
        this.title = title;
        this.description = description;
        this.status = status;
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
        return String.format("Task #%d\nStatus: %s\nTitle: %s\n%s\n", id, status, title, description);
    }

    @Override
    public int compareTo(Task o) {
        return id - o.getId();
    }

    public enum Status {
        NEW,
        IN_PROGRESS,
        DONE
    }
}
