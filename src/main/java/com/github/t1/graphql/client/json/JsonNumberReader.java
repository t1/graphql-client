package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.api.GraphQlClientException;
import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.json.JsonNumber;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.function.Function;

import static com.github.t1.graphql.client.json.GraphQlClientValueException.check;
import static java.util.Arrays.asList;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
class JsonNumberReader implements Reader<JsonNumber> {
    private final TypeInfo type;

    @Override public Object read(Location location, JsonNumber value) {
        for (MatchReader<JsonNumber> reader : SUB_READERS)
            if (reader.matches(type.getRawType()))
                return reader.read(location, value);
        throw new GraphQlClientException("can't map number '" + value + "' to " + type);
    }

    private final List<MatchReader<JsonNumber>> SUB_READERS = asList(
        new IntReader(Byte.class, i -> (byte) (long) i),
        new IntReader(char.class, Character.class, Character.MIN_VALUE, Character.MAX_VALUE, i -> (char) (long) i),
        new IntReader(Short.class, i -> (short) (long) i),
        new IntReader(Integer.class, i -> (int) (long) i),
        new LongReader(),
        new FloatReader(),
        new DoubleReader(),
        new BigIntegerReader(),
        new BigDecimalReader()
    );

    @RequiredArgsConstructor
    private static class IntReader implements MatchReader<JsonNumber> {
        private final Class<?> type;
        private final Class<?> primitive;
        private final long minValue;
        private final long maxValue;
        private final Function<Long, Object> cast;

        public IntReader(Class<?> type, Function<Long, Object> cast) {
            this(type,
                readConstant(type, "TYPE"),
                readNumberConstant(type, "MIN_VALUE"),
                readNumberConstant(type, "MAX_VALUE"),
                cast);
        }

        @Override public boolean matches(Class<?> rawType) {
            return primitive.equals(rawType) || type.equals(rawType);
        }

        @Override public Object read(Location location, JsonNumber value) {
            long longValue = value.longValue();
            check(location, value, longValue >= minValue);
            check(location, value, longValue <= maxValue);
            return cast.apply(longValue);
        }
    }

    private static class LongReader implements MatchReader<JsonNumber> {
        @Override public boolean matches(Class<?> rawType) {
            return long.class.equals(rawType) || Long.class.equals(rawType);
        }

        @Override public Object read(Location location, JsonNumber value) {
            try {
                return value.longValueExact();
            } catch (ArithmeticException e) {
                throw new GraphQlClientValueException(location, value);
            }
        }
    }

    private static class FloatReader implements MatchReader<JsonNumber> {
        @Override public boolean matches(Class<?> rawType) {
            return float.class.equals(rawType) || Float.class.equals(rawType);
        }

        @Override public Object read(Location location, JsonNumber value) {
            return (float) value.doubleValue();
        }
    }

    private static class DoubleReader implements MatchReader<JsonNumber> {
        @Override public boolean matches(Class<?> rawType) {
            return double.class.equals(rawType) || Double.class.equals(rawType);
        }

        @Override public Object read(Location location, JsonNumber value) {
            return value.doubleValue();
        }
    }

    private static class BigIntegerReader implements MatchReader<JsonNumber> {
        @Override public boolean matches(Class<?> rawType) {
            return BigInteger.class.equals(rawType);
        }

        @Override public Object read(Location location, JsonNumber value) {
            return value.bigIntegerValueExact();
        }
    }

    private static class BigDecimalReader implements MatchReader<JsonNumber> {
        @Override public boolean matches(Class<?> rawType) {
            return BigDecimal.class.equals(rawType);
        }

        @Override public Object read(Location location, JsonNumber value) {
            return value.bigDecimalValue();
        }
    }

    private static long readNumberConstant(Class<?> type, String fieldName) {
        return JsonNumberReader.<Number>readConstant(type, fieldName).longValue();
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private static <T> T readConstant(Class<?> type, String fieldName) {
        Field field = type.getField(fieldName);
        assert Modifier.isStatic(field.getModifiers());
        field.setAccessible(true);
        //noinspection unchecked
        return (T) field.get(null);
    }
}
