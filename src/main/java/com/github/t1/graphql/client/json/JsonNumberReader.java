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
import java.util.function.Supplier;

import static java.util.Arrays.asList;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
class JsonNumberReader implements Supplier<Object> {
    private final TypeInfo type;
    private final JsonNumber value;

    @Override public Object get() {
        for (SubReader sub : SUB_READERS)
            if (sub.matches())
                return sub.read();
        if (long.class.equals(type.getRawType()) || Long.class.equals(type.getRawType()))
            return value.longValueExact();
        if (float.class.equals(type.getRawType()) || Float.class.equals(type.getRawType()))
            return (float) value.doubleValue();
        if (double.class.equals(type.getRawType()) || Double.class.equals(type.getRawType()))
            return value.doubleValue();
        if (BigInteger.class.equals(type.getRawType()))
            return value.bigIntegerValueExact();
        if (BigDecimal.class.equals(type.getRawType()))
            return value.bigDecimalValue();
        throw new GraphQlClientException("can't map number '" + value + "' to " + type);
    }

    private final List<SubReader> SUB_READERS = asList(
        new SubReader(Byte.class, i -> (byte) (long) i),
        new SubReader(char.class, Character.class, Character.MIN_VALUE, Character.MAX_VALUE, i -> (char) (long) i),
        new SubReader(Short.class, i -> (short) (long) i),
        new SubReader(Integer.class, i -> (int) (long) i)
    );

    @RequiredArgsConstructor
    private class SubReader {
        private final Class<?> type;
        private final Class<?> primitive;
        private final long minValue;
        private final long maxValue;
        private final Function<Long, Object> cast;

        public SubReader(Class<?> type, Function<Long, Object> cast) {
            this(type,
                readConstant(type, "TYPE"),
                readNumberConstant(type, "MIN_VALUE"),
                readNumberConstant(type, "MAX_VALUE"),
                cast);
        }

        public boolean matches() {
            return primitive.equals(JsonNumberReader.this.type.getRawType()) || type.equals(JsonNumberReader.this.type.getRawType());
        }

        public Object read() {
            long value = JsonNumberReader.this.value.longValue();
            check(value >= minValue);
            check(value <= maxValue);
            return cast.apply(value);
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

    private void check(boolean value) {
        if (!value)
            throw new GraphQlClientException("invalid value for " + type + " field "
                + "code"/* TODO field name */ + ": " + this.value);
    }
}
