package com.github.t1.graphql.client.impl.reflection;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;

import static lombok.AccessLevel.PACKAGE;

@EqualsAndHashCode
@RequiredArgsConstructor(access = PACKAGE)
public class FieldInfo {
    private final TypeInfo container;
    private final Field field;

    @Override public String toString() { return "field '" + field.getName() + "' in " + container; }

    public TypeInfo getType() {
        return new TypeInfo(container, field.getGenericType());
    }

    public String getName() { return field.getName(); }
}
