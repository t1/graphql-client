package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;

import javax.json.JsonObject;
import javax.json.JsonValue;

import static com.github.t1.graphql.client.json.JsonReader.readJson;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
class JsonObjectReader implements Reader<JsonObject> {
    private final TypeInfo type;

    @Override public Object read(Location location, JsonObject value) {
        Object instance = type.newInstance();
        type.fields().forEach(field -> {
            Location fieldLocation = new Location(field.getType(), location.getDescription() + "." + field.getName());
            JsonValue jsonFieldValue = value.get(field.getName());
            Object javaFieldValue = readJson(fieldLocation, field.getType(), jsonFieldValue);
            field.set(instance, javaFieldValue);
        });
        return instance;
    }
}
