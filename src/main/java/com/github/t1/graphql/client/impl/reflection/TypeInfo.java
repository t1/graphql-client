package com.github.t1.graphql.client.impl.reflection;

import com.github.t1.graphql.client.api.GraphQlClientException;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
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
            || isOptional() // MP GraphQL represents Optionals as Arrays with zero or one items
            || Collection.class.isAssignableFrom(raw(type));
    }

    public boolean isOptional() {
        return Optional.class.equals(raw(type));
    }

    /** Types that JSON-B can convert to from JSON */
    public boolean isConvertible() {
        return CONVERTIBLE_TYPES.contains(raw(type));
    }

    public TypeInfo itemType() {
        assert isCollection();
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


    public boolean isEnum() { return ifClass(Class::isEnum); }

    private boolean ifClass(Predicate<Class<?>> predicate) {
        return (type instanceof Class) && predicate.test((Class<?>) type);
    }

    public boolean isScalar() {
        return SCALAR_TYPES.contains(type);
    }

    private static final List<Type> SCALAR_TYPES = asList(
        Integer.class, int.class,
        Double.class, double.class, // = Float in GraphQL speech
        String.class, // includes ID
        Boolean.class, boolean.class
    );

    public Type getNativeType() { return type; }

    public Type[] getNativeTypeArguments() {
        return ((ParameterizedType) type).getActualTypeArguments();
    }

    public static final List<Class<?>> CONVERTIBLE_TYPES = asList(
        LocalDate.class, LocalDateTime.class, LocalTime.class,
        ZonedDateTime.class, Instant.class, OffsetDateTime.class,
        Year.class, Month.class, DayOfWeek.class, MonthDay.class, YearMonth.class
        // TODO maybe more?
    );
}
