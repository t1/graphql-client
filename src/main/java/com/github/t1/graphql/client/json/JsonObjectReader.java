package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.api.GraphQlClientException;
import com.github.t1.graphql.client.reflection.FieldInfo;
import com.github.t1.graphql.client.reflection.TypeInfo;

import javax.json.JsonObject;
import javax.json.JsonValue;

import static com.github.t1.graphql.client.json.GraphQlClientValueException.check;
import static com.github.t1.graphql.client.json.JsonReader.readJson;

class JsonObjectReader extends Reader<JsonObject> {
    JsonObjectReader(TypeInfo type, Location location, JsonObject value) { super(type, location, value); }

    @Override Object read() {
        check(location, value, !type.isCollection() && !type.isScalar());
        Object instance = newInstance();
        type.fields().forEach(field -> {
            Object fieldValue = buildValue(location, value, field);
            field.set(instance, fieldValue);
        });
        return instance;
    }

    private Object newInstance() {
        try {
            return type.newInstance();
        } catch (Exception e) {
            throw new GraphQlClientException("can't create " + location, e);
        }
    }

    private Object buildValue(Location location, JsonObject value, FieldInfo field) {
        Location fieldLocation = new Location(field.getType(), location.getDescription() + "." + field.getName());
        JsonValue jsonFieldValue = value.get(field.getName());
        return readJson(fieldLocation, field.getType(), jsonFieldValue);
    }
}
