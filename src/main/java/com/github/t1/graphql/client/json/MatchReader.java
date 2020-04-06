package com.github.t1.graphql.client.json;

import javax.json.JsonValue;

interface MatchReader<T extends JsonValue> extends Reader<T> {
    default boolean matches(Class<?> rawType) { throw new UnsupportedOperationException(); }
}
