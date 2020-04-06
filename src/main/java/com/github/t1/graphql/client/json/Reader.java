package com.github.t1.graphql.client.json;

import javax.json.JsonValue;

interface Reader<T extends JsonValue> {
    Object read(Location location, T value);
}
