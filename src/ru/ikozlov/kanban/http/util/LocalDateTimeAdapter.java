package ru.ikozlov.kanban.http.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter writer, LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            writer.nullValue();
            return;
        }
        writer.value(localDateTime.toString());
    }

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL) {
            String value = reader.nextString();
            return LocalDateTime.parse(value);
        } else {
            return null;
        }
    }
}