package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;

import javax.json.JsonValue;

import static com.github.t1.graphql.client.json.GraphQlClientValueException.check;
import static javax.json.JsonValue.ValueType.FALSE;
import static javax.json.JsonValue.ValueType.TRUE;

class JsonBooleanReader extends Reader<JsonValue> {
    JsonBooleanReader(TypeInfo type, Location location, JsonValue value) { super(type, location, value); }

    @Override Object read() {
        assert value.getValueType() == TRUE || value.getValueType() == FALSE;
        check(location, value, boolean.class.equals(type.getRawType()) || Boolean.class.equals(type.getRawType()));
        return value.getValueType() == TRUE;
    }
}
