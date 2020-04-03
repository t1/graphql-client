package com.github.t1.graphql.client.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import java.util.function.Supplier;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor
@NoArgsConstructor(access = PACKAGE, force = true)
public @Data class GraphQlClientHeader {
    private final String name;
    private final Supplier<Object> supplier;

    public GraphQlClientHeader(String name, Object value) { this(name, () -> value); }

    public Object getValue() {
        assert supplier != null;
        return supplier.get();
    }

    public Map.Entry<String, Object> toEntry() { return new SimpleEntry<>(name, getValue()); }
}
