package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
class Location {
    private final TypeInfo type;
    private final String description;

    @Override public String toString() { return type.getTypeName() + " value for " + description; }
}
