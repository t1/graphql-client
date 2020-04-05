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
        for (NumberReader sub : SUB_READERS)
            if (sub.matches())
                return sub.read();
        throw new GraphQlClientException("can't map number '" + value + "' to " + type);
    }

    private final List<NumberReader> SUB_READERS = asList(
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

    public interface NumberReader {
        boolean matches();

        Object read();
    }

    @RequiredArgsConstructor
    private class IntReader implements NumberReader {
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

        @Override public boolean matches() {
            return primitive.equals(JsonNumberReader.this.type.getRawType()) || type.equals(JsonNumberReader.this.type.getRawType());
        }

        @Override public Object read() {
            long value = JsonNumberReader.this.value.longValue();
            check(value >= minValue);
            check(value <= maxValue);
            return cast.apply(value);
        }
    }

    private class LongReader implements NumberReader {
        @Override public boolean matches() {
            return long.class.equals(type.getRawType()) || Long.class.equals(type.getRawType());
        }

        @Override public Object read() {
            try {
                return value.longValueExact();
            } catch (ArithmeticException e) {
                throw invalidValueException();
            }
        }
    }

    private class FloatReader implements NumberReader {
        @Override public boolean matches() {
            return float.class.equals(type.getRawType()) || Float.class.equals(type.getRawType());
        }

        @Override public Object read() {
            return (float) value.doubleValue();
        }
    }

    private class DoubleReader implements NumberReader {
        @Override public boolean matches() {
            return double.class.equals(type.getRawType()) || Double.class.equals(type.getRawType());
        }

        @Override public Object read() {
            return value.doubleValue();
        }
    }

    private class BigIntegerReader implements NumberReader {
        @Override public boolean matches() {
            return BigInteger.class.equals(type.getRawType());
        }

        @Override public Object read() {
            return value.bigIntegerValueExact();
        }
    }

    private class BigDecimalReader implements NumberReader {
        @Override public boolean matches() {
            return BigDecimal.class.equals(type.getRawType());
        }

        @Override public Object read() {
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

    private void check(boolean value) {
        if (!value)
            throw invalidValueException();
    }

    private GraphQlClientException invalidValueException() {
        return new GraphQlClientException("invalid value for " + type + " field "
            + "code"/* TODO field name */ + ": " + this.value);
    }
}
