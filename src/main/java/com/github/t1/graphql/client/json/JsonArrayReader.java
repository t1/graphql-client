package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;

import javax.json.JsonArray;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static com.github.t1.graphql.client.CollectionUtils.toArray;
import static com.github.t1.graphql.client.json.JsonReader.readJson;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
class JsonArrayReader implements Supplier<Object> {
    private final TypeInfo type;
    private final JsonArray value;

    @Override public Object get() {
        return value.stream().map(v -> readJson(type.getItemType(), v)).collect(collector());
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
