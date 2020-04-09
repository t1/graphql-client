package com.github.t1.graphql.client.reflection;

import com.github.t1.graphql.client.api.GraphQlClientException;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;

import java.lang.reflect.Field;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
public class FieldInfo {
    private final TypeInfo container;
    private final Field field;

    @Override public String toString() { return "field '" + field.getName() + "' in " + container; }

    public TypeInfo getType() {
        return new TypeInfo(container, field.getGenericType());
    }

    public String getName() {
        if (field.isAnnotationPresent(Name.class))
            return field.getAnnotation(Name.class).value();
        return field.getName();
    }

    public Object get(Object instance) {
        try {
            field.setAccessible(true);
            return field.get(instance);
        } catch (ReflectiveOperationException e) {
            // this code is unreachable: setAccessible also allows to change `final` fields
            throw new GraphQlClientException("can't get field " + this, e);
        }
    }

    public void set(Object instance, Object value) {
        try {
            field.setAccessible(true);
            field.set(instance, value);
        } catch (ReflectiveOperationException e) {
            // this code is unreachable: setAccessible also allows to change `final` fields
            throw new GraphQlClientException("can't set field " + this + " to " + value, e);
        }
    }

    public boolean isNonNull() {
        return field.isAnnotationPresent(NonNull.class) || getType().isPrimitive();
    }
}
