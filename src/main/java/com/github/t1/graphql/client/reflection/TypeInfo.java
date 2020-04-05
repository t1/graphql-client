package com.github.t1.graphql.client.reflection;

import com.github.t1.graphql.client.api.GraphQlClientException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
public class TypeInfo {
    private final TypeInfo container;
    private final @NonNull Type type;

    @Getter(lazy = true) private final TypeInfo itemType
        = new TypeInfo(this, computeItemType());
    @Getter(lazy = true) private final Class<?> rawType = raw(type);

    private Type computeItemType() {
        assert isCollection() || isOptional();
        if (type instanceof ParameterizedType)
            return ((ParameterizedType) type).getActualTypeArguments()[0];
        assert type != null;
        return ((Class<?>) type).getComponentType();
    }


    private Class<?> raw(Type type) {
        if (type instanceof Class)
            return (Class<?>) type;
        if (type instanceof ParameterizedType)
            return raw(((ParameterizedType) type).getRawType());
        if (type instanceof TypeVariable)
            return resolveTypeVariable();
        throw new GraphQlClientException("unsupported reflection type " + type.getClass());
    }

    private Class<?> resolveTypeVariable() {
        // TODO this is not generally correct
        ParameterizedType parameterizedType = (ParameterizedType) container.type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }


    @Override public String toString() { return type + ((container == null) ? "" : " in " + container); }

    public boolean isCollection() {
        return ifClass(Class::isArray)
            || Collection.class.isAssignableFrom(getRawType());
    }

    private boolean ifClass(Predicate<Class<?>> predicate) {
        return (type instanceof Class) && predicate.test((Class<?>) type);
    }

    public Stream<FieldInfo> fields() {
        Field[] declaredFields = getRawType().getDeclaredFields();
        return Stream.of(declaredFields)
            .filter(field -> !isStatic(field.getModifiers()))
            .filter(field -> !isTransient(field.getModifiers()))
            .map(field -> new FieldInfo(this, field));
    }

    public boolean isOptional() {
        return Optional.class.equals(getRawType());
    }

    public boolean isScalar() {
        return getRawType().isPrimitive()
            || Character.class.equals(getRawType()) // has a valueOf(char), not valueOf(String)
            || CharSequence.class.isAssignableFrom(getRawType())
            || isEnum()
            || scalarConstructor().isPresent();
    }

    public boolean isEnum() {
        return ifClass(Class::isEnum);
    }

    public Optional<Executable> scalarConstructor() {
        return Stream.concat(
            Stream.of(getRawType().getConstructors()).filter(this::hasOneStringParameter),
            Stream.of(getRawType().getMethods()).filter(this::isStaticStringConstructor)
        ).findFirst();
    }

    private boolean hasOneStringParameter(Executable executable) {
        return executable.getParameterCount() == 1 && CharSequence.class.isAssignableFrom(executable.getParameterTypes()[0]);
    }

    private boolean isStaticStringConstructor(Method method) {
        return isStaticConstructorMethodNamed(method, "parse")
            || isStaticConstructorMethodNamed(method, "valueOf");
    }

    private boolean isStaticConstructorMethodNamed(Method method, String name) {
        return method.getName().equals(name)
            && Modifier.isStatic(method.getModifiers())
            && method.getReturnType().equals(type)
            && hasOneStringParameter(method);
    }
}
