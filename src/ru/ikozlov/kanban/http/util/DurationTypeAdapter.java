package ru.ikozlov.kanban.http.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter writer, Duration duration) throws IOException {
        if (duration == null) {
            writer.nullValue();
            return;
        }
        writer.value(duration.toString());
    }

    @Override
    public Duration read(JsonReader reader) throws IOException {
        if (reader.peek() != JsonToken.NULL) {
            String value = reader.nextString();
            return Duration.parse(value);
        } else {
            return null;
        }
    }
}