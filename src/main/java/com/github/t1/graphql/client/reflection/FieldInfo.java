package com.github.t1.graphql.client.reflection;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.json.bind.annotation.JsonbProperty;
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

    public String getName() {
        if (field.isAnnotationPresent(JsonbProperty.class))
            return field.getAnnotation(JsonbProperty.class).value();
        return field.getName();
    }
}
