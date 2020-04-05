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
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class JsonReader {
    public static Object readJson(TypeInfo type, JsonValue value) {
        return new JsonReader(type, value).read();
    }

    private final TypeInfo type;
    private final JsonValue value;

    private Object read() {
        if (type.isOptional())
            return Optional.ofNullable(readJson(type.getItemType(), value));
        return reader().get();
    }

    private Supplier<Object> reader() {
        switch (value.getValueType()) {
            case ARRAY:
                return new JsonArrayReader(type, (JsonArray) value);
            case OBJECT:
                return new JsonObjectReader(type, (JsonObject) value);
            case STRING:
                return new JsonStringReader(type, (JsonString) value);
            case NUMBER:
                return new JsonNumberReader(type, (JsonNumber) value);
            case TRUE:
            case FALSE:
                return new JsonBooleanReader(type, value);
            case NULL:
                return () -> null;
        }
        throw new GraphQlClientException("unreachable code");
    }
}
