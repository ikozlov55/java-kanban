package ru.ikozlov.kanban.task;

public class Subtask extends Task {
    private Epic epic;

    public Subtask(String title, String description, Status status, Epic epic) {
        super(title, description, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        Epic epicCopy = new Epic(epic.getTitle(), epic.getDescription(), epic.getSubtasks());
        epicCopy.setId(epic.getId());
        return epicCopy;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }


    @Override
    public String toString() {
        return String.format("Subtask #%d\nEpic: %d\nStatus: %s\nTitle: %s\n%s\n",
                getId(), epic.getId(), getStatus(), getTitle(), getDescription());
    }

    public Subtask copy() {
        Epic epicCopy = getEpic();
        Subtask copy = new Subtask(getTitle(), getDescription(), getStatus(), epicCopy);
        copy.setId(getId());
        return copy;
    }
}
