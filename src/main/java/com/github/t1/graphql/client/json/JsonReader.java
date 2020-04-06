package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.api.GraphQlClientException;
import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.util.Optional;

import static com.github.t1.graphql.client.json.GraphQlClientValueException.check;
import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class JsonReader {
    public static Object readJson(Location location, TypeInfo type, JsonValue value) {
        return new JsonReader(type, value).read(location);
    }

    private final TypeInfo type;
    private final JsonValue value;

    private Object read(Location location) {
        if (type.isOptional())
            return Optional.ofNullable(readJson(location, type.getItemType(), value));
        return reader(location);
    }

    private Object reader(Location location) {
        switch (value.getValueType()) {
            case ARRAY:
                return new JsonArrayReader(type).read(location, (JsonArray) value);
            case OBJECT:
                return new JsonObjectReader(type).read(location, (JsonObject) value);
            case STRING:
                return new JsonStringReader(type).read(location, (JsonString) value);
            case NUMBER:
                return new JsonNumberReader(type).read(location, (JsonNumber) value);
            case TRUE:
            case FALSE:
                return new JsonBooleanReader(type).read(location, value);
            case NULL:
                check(location, value, !type.isPrimitive());
                return null;
        }
        throw new GraphQlClientException("unreachable code");
    }
}
