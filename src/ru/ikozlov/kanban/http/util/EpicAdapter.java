package ru.ikozlov.kanban.http.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ru.ikozlov.kanban.task.Epic;
import ru.ikozlov.kanban.task.Subtask;
import ru.ikozlov.kanban.task.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicAdapter extends TypeAdapter<Epic> {

    @Override
    public void write(JsonWriter writer, Epic epic) throws IOException {
        writer.beginObject()
                .name("id").value(epic.getId())
                .name("title").value(epic.getTitle())
                .name("description").value(epic.getDescription())
                .name("status").value(epic.getStatus().name())
                .name("duration").value(epic.getDuration().toString())
                .name("startTime").value(epic.getStartTime() != null ? epic.getStartTime().toString() : null)
                .name("endTime").value(epic.getEndTime() != null ? epic.getEndTime().toString() : null)
                .name("subtasks");
        writeSubtasks(writer, epic.getSubtasks());
        writer.endObject();
    }

    private void writeSubtasks(JsonWriter writer, List<Subtask> subtasks) throws IOException {
        writer.beginArray();
        for (Subtask subtask : subtasks) {
            writer.beginObject()
                    .name("id").value(subtask.getId())
                    .name("epicId").value(subtask.getEpicId())
                    .name("title").value(subtask.getTitle())
                    .name("description").value(subtask.getDescription())
                    .name("status").value(subtask.getStatus().name())
                    .name("duration").value(subtask.getDuration().toString())
                    .name("startTime").value(subtask.getStartTime().toString())
                    .endObject();
        }
        writer.endArray();
    }

    @Override
    public Epic read(JsonReader reader) throws IOException {
        Integer id = null;
        String title = "";
        String description = "";
        List<Subtask> subtasks = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id" -> {
                    if (reader.peek() != JsonToken.NULL) {
                        id = reader.nextInt();
                    } else {
                        reader.skipValue();
                    }
                }
                case "title" -> title = reader.nextString();
                case "description" -> description = reader.nextString();
                case "status", "duration", "startTime", "endTime" -> reader.skipValue();
                case "subtasks" -> subtasks = readSubtasksList(reader);
            }
        }
        reader.endObject();
        Epic epic = new Epic(title, description, subtasks);
        if (id != null) {
            epic.setId(id);
        }
        return epic;
    }

    public List<Subtask> readSubtasksList(JsonReader reader) throws IOException {
        List<Subtask> subtasks = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            subtasks.add(readSubtask(reader));
        }
        reader.endArray();
        return subtasks;
    }

    public Subtask readSubtask(JsonReader reader) throws IOException {
        Integer id = null;
        int epicId = 0;
        String title = null;
        Task.Status status = null;
        String description = null;
        Duration duration = null;
        LocalDateTime startTime = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "id" -> {
                    if (reader.peek() != JsonToken.NULL) {
                        id = reader.nextInt();
                    } else {
                        reader.skipValue();
                    }
                }
                case "title" -> title = reader.nextString();
                case "epicId" -> epicId = reader.nextInt();
                case "description" -> description = reader.nextString();
                case "status" -> status = Task.Status.valueOf(reader.nextString());
                case "duration" -> duration = Duration.parse(reader.nextString());
                case "startTime" -> {
                    if (reader.peek() != JsonToken.NULL) {
                        startTime = LocalDateTime.parse(reader.nextString());
                    } else {
                        reader.skipValue();
                    }
                }
            }
        }
        reader.endObject();
        Subtask subtask = new Subtask(title, description, status, epicId, duration, startTime);
        if (id != null) {
            subtask.setId(id);
        }
        return subtask;
    }

}

