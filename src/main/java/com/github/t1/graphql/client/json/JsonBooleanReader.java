package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;

import javax.json.JsonValue;
import java.util.function.Supplier;

import static javax.json.JsonValue.ValueType.FALSE;
import static javax.json.JsonValue.ValueType.TRUE;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
public class JsonBooleanReader implements Supplier<Object> {
    private final TypeInfo type;
    private final JsonValue value;

    @Override public Object get() {
        assert value.getValueType() == TRUE || value.getValueType() == FALSE;
        assert (boolean.class.equals(type.getRawType()) || Boolean.class.equals(type.getRawType()));
        return value.getValueType() == TRUE;
    }
}
