package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.Value;

@Value
class Location {
    TypeInfo type;
    String description;

    @Override public String toString() { return type.getTypeName() + " value for " + description; }
}
