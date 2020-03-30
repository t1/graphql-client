package com.github.t1.graphql.client.reflection;

import com.github.t1.graphql.client.api.GraphQlClientException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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
        return ifClass(Class::isArray)
            || Collection.class.isAssignableFrom(raw(type));
    }

    private boolean ifClass(Predicate<Class<?>> predicate) {
        return (type instanceof Class) && predicate.test((Class<?>) type);
    }

    public TypeInfo itemType() {
        assert isCollection() || isWrapped();
        Type itemType = (type instanceof ParameterizedType)
            ? ((ParameterizedType) type).getActualTypeArguments()[0]
            : ((Class<?>) type).getComponentType();
        return new TypeInfo(this, itemType);
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
            return resolveTypeVariable();
        throw new GraphQlClientException("unsupported reflection type " + type.getClass());
    }

    private Class<?> resolveTypeVariable() {
        // TODO this is not generally correct
        ParameterizedType parameterizedType = (ParameterizedType) container.type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }

    public boolean isWrapped() {
        return Optional.class.equals(raw(type));
    }

    public boolean isScalar() {
        return PRIMITIVE_SCALAR_TYPES.contains(type) || ifClass(Class::isEnum) || ifClass(this::isScalar);
    }

    private static final List<Type> PRIMITIVE_SCALAR_TYPES = asList(
        int.class,
        double.class, // = Float in GraphQL speech
        String.class, // includes ID
        boolean.class
    );

    private boolean isScalar(Class<?> type) {
        return Stream.of(type.getConstructors()).anyMatch(this::hasOneStringParameter)
            || Stream.of(type.getMethods()).anyMatch(this::isStaticStringConstructor);
    }

    private boolean hasOneStringParameter(Executable executable) {
        return executable.getParameterCount() == 1 && CharSequence.class.isAssignableFrom(executable.getParameterTypes()[0]);
    }

    private boolean isStaticStringConstructor(Method method) {
        return isStaticConstructorMethodNamed(method, "parse")
            || isStaticConstructorMethodNamed(method, "valueOf");
    }

    private boolean isStaticConstructorMethodNamed(Method method, String name) {
        return method.getName().equals(name) && Modifier.isStatic(method.getModifiers())
            && method.getReturnType().equals(type)
            && hasOneStringParameter(method);
    }

    public Type getNativeType() { return type; }
}
