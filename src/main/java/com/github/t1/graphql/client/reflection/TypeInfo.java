package com.github.t1.graphql.client.reflection;

import com.github.t1.graphql.client.api.GraphQlClientException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static com.github.t1.graphql.client.CollectionUtils.toArray;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static javax.json.JsonValue.ValueType.NULL;
import static lombok.AccessLevel.PACKAGE;

@EqualsAndHashCode
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
        return PRIMITIVE_SCALAR_TYPES.contains(type) || isEnum() || scalarConstructor().isPresent();
    }

    private static final List<Type> PRIMITIVE_SCALAR_TYPES = asList(
        int.class,
        double.class, // = Float in GraphQL speech
        String.class, // includes ID
        boolean.class
    );

    private boolean isEnum() {
        return ifClass(Class::isEnum);
    }

    private Optional<Executable> scalarConstructor() {
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
        return method.getName().equals(name) && Modifier.isStatic(method.getModifiers())
            && method.getReturnType().equals(type)
            && hasOneStringParameter(method);
    }

    public Object fromJson(JsonValue in) {
        if (isOptional())
            return Optional.ofNullable(getItemType().fromJson(in));
        if (in.getValueType() == NULL)
            return null;
        if (isScalar())
            return fromScalar(in);
        if (isCollection())
            return fromCollection((JsonArray) in);
        return fromObject((JsonObject) in);
    }

    private Object fromScalar(JsonValue in) {
        if (int.class.equals(type) || Integer.class.equals(type))
            return ((JsonNumber) in).intValue();
        if (BigInteger.class.equals(type))
            return ((JsonNumber) in).bigIntegerValueExact();
        if (BigDecimal.class.equals(type))
            return ((JsonNumber) in).bigDecimalValue();
        if (boolean.class.equals(type) || Boolean.class.equals(type))
            switch (in.getValueType()) {
                case TRUE:
                    return true;
                case FALSE:
                    return false;
                default:
                    throw new GraphQlClientException("expected JSON boolean but found " + in.getValueType());
            }
        if (double.class.equals(type) || Double.class.equals(type))
            return ((JsonNumber) in).doubleValue();
        if (String.class.equals(type))
            return ((JsonString) in).getString();
        if (isEnum())
            //noinspection rawtypes,unchecked
            return Enum.valueOf((Class) getRawType(), ((JsonString) in).getString());
        // ifClass(Class::isEnum)
        Executable executable = scalarConstructor().orElseThrow(() -> new GraphQlClientException("expected a scalar constructor on " + type));
        return execute(executable, ((JsonString) in).getString());
    }

    private Object execute(Executable executable, String string) {
        try {
            if (executable instanceof Method) {
                return ((Method) executable).invoke(null, string);
            } else {
                return ((Constructor<?>) executable).newInstance(string);
            }
        } catch (ReflectiveOperationException e) {
            throw new GraphQlClientException("can't create " + type, e);
        }
    }

    private Object fromCollection(JsonArray in) {
        return in.stream().map(getItemType()::fromJson).collect(collector());
    }

    private Collector<Object, ?, ?> collector() {
        if (getRawType().isArray()) {
            @SuppressWarnings("unchecked")
            Class<Object> rawItemType = (Class<Object>) getItemType().getRawType();
            return toArray(rawItemType);
        }
        if (Set.class.isAssignableFrom(getRawType()))
            return toSet();
        assert List.class.isAssignableFrom(getRawType());
        return toList();
    }

    private Object fromObject(JsonObject in) {
        try {
            Object instance = getRawType().getConstructor().newInstance();
            fields().forEach(field -> {
                JsonValue jsonFieldValue = in.get(field.getName());
                Object javaFieldValue = field.getType().fromJson(jsonFieldValue);
                field.set(instance, javaFieldValue);
            });
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new GraphQlClientException("can't create " + type, e);
        }
    }
}
