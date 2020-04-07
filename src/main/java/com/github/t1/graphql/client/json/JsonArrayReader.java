package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;

import javax.json.JsonArray;
import javax.json.JsonValue;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;

import static com.github.t1.graphql.client.CollectionUtils.toArray;
import static com.github.t1.graphql.client.json.GraphQlClientValueException.check;
import static com.github.t1.graphql.client.json.JsonReader.readJson;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

class JsonArrayReader extends Reader<JsonArray> {
    JsonArrayReader(TypeInfo type, Location location, JsonArray value) { super(type, location, value); }

    @Override Object read() {
        check(location, value, type.isCollection());
        IndexedLocationBuilder locationBuilder = new IndexedLocationBuilder(location);
        return value.stream().map(item -> readItem(locationBuilder, item)).collect(collector());
    }

    private Object readItem(IndexedLocationBuilder locationBuilder, JsonValue value) {
        return readJson(locationBuilder.nextLocation(), type.getItemType(), value);
    }

    private Collector<Object, ?, ?> collector() {
        if (type.getRawType().isArray()) {
            @SuppressWarnings("unchecked")
            Class<Object> rawItemType = (Class<Object>) type.getItemType().getRawType();
            return toArray(rawItemType);
        }
        if (Set.class.isAssignableFrom(type.getRawType()))
            return toSet();
        assert List.class.isAssignableFrom(type.getRawType());
        return toList();
    }
}
