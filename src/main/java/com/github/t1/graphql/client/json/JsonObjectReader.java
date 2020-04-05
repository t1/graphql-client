package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.api.GraphQlClientException;
import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.function.Supplier;

import static com.github.t1.graphql.client.json.JsonReader.readJson;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
class JsonObjectReader implements Supplier<Object> {
    private final TypeInfo type;
    private final JsonObject value;

    @Override public Object get() {
        Object instance = newInstance();
        type.fields().forEach(field -> {
            JsonValue jsonFieldValue = value.get(field.getName());
            Object javaFieldValue = readJson(field.getType(), jsonFieldValue);
            field.set(instance, javaFieldValue);
        });
        return instance;
    }

    private Object newInstance() {
        try {
            return type.getRawType().getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new GraphQlClientException("can't create " + type, e);
        }
    }
}
