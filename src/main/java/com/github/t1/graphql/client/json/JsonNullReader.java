package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;

import javax.json.JsonValue;

import static com.github.t1.graphql.client.json.GraphQlClientValueException.check;

class JsonNullReader extends Reader<JsonValue> {
    JsonNullReader(TypeInfo type, Location location, JsonValue value) { super(type, location, value); }

    @Override Object read() {
        check(location, value, !type.isPrimitive());
        return null;
    }
}
