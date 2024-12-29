package ru.ikozlov.kanban;

public class Subtask extends Task {
    private Epic epic;

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Subtask(String title, String description, Status status, Epic epic) {
        super(title, description, status);
        this.epic = epic;
    }

    @Override
    public String toString() {
        return String.format("Subtask #%d\nEpic: %d\nStatus: %s\nTitle: %s\n%s\n",
                getId(), epic.getId(), getStatus(), getTitle(), getDescription());
    }


}
