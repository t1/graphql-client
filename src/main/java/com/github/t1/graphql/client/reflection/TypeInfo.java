package com.github.t1.graphql.client.reflection;

import com.github.t1.graphql.client.GraphQlClientException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PACKAGE;

@EqualsAndHashCode
@RequiredArgsConstructor(access = PACKAGE)
public class TypeInfo {
    private final TypeInfo container;
    private final Type type;

    @Override public String toString() { return type + ((container == null) ? "" : " in " + container); }

    public boolean isCollection() {
        if (!(type instanceof ParameterizedType)) {
            return false;
        }
        return Collection.class.isAssignableFrom(raw(type));
    }

    public TypeInfo itemType() {
        assert isCollection();
        return new TypeInfo(this, ((ParameterizedType) type).getActualTypeArguments()[0]);
    }

    public Stream<FieldInfo> fields() {
        Field[] declaredFields = raw(type).getDeclaredFields();
        return Stream.of(declaredFields)
            .filter(field -> !isStatic(field.getModifiers()))
            .map(field -> new FieldInfo(this, field));
    }

    private Class<?> raw(Type type) {
        if (type instanceof Class)
            return (Class<?>) type;
        if (type instanceof ParameterizedType)
            return raw(((ParameterizedType) type).getRawType());
        if (type instanceof TypeVariable)
            return resolveTypeVariable((TypeVariable<?>) type);
        throw new GraphQlClientException("unsupported reflection type " + type.getClass());
    }

    private Class<?> resolveTypeVariable(TypeVariable<?> typeVariable) {
        // TODO this is not generally correct
        if (container.type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) container.type;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length >= 1 && actualTypeArguments[0] instanceof Class) {
                return (Class<?>) actualTypeArguments[0];
            }
        }
        return raw(typeVariable.getBounds()[0]);
    }

    public boolean isScalar() {
        return SCALAR_TYPES.contains(type);
    }

    private static final List<Type> SCALAR_TYPES = asList(
        String.class, Integer.class, int.class
        // TODO other scalar types
    );

    public Type getNativeType() { return type; }
}
