package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.json.JsonValue;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
abstract class Reader<T extends JsonValue> {
    protected final TypeInfo type;
    protected final Location location;
    protected final @NonNull T value;

    abstract Object read();
}
