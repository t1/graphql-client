package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;

import javax.json.JsonValue;

import static javax.json.JsonValue.ValueType.FALSE;
import static javax.json.JsonValue.ValueType.TRUE;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
public class JsonBooleanReader implements Reader<JsonValue> {
    private final TypeInfo type;

    @Override public Object read(Location location, JsonValue value) {
        assert value.getValueType() == TRUE || value.getValueType() == FALSE;
        assert (boolean.class.equals(type.getRawType()) || Boolean.class.equals(type.getRawType()));
        return value.getValueType() == TRUE;
    }
}
