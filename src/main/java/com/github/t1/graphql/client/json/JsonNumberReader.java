package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;

import javax.json.JsonNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.github.t1.graphql.client.json.GraphQlClientValueException.check;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
class JsonNumberReader implements Reader<JsonNumber> {
    private final TypeInfo type;

    @Override public Object read(Location location, JsonNumber value) {
        try {
            return read(location, value, type.getRawType());
        } catch (ArithmeticException e) {
            throw new GraphQlClientValueException(location, value, e);
        }
    }

    private Object read(Location location, JsonNumber value, Class<?> rawType) {
        if (byte.class.equals(rawType) || Byte.class.equals(rawType))
            return (byte) readIntBetween(location, value, Byte.MIN_VALUE, Byte.MAX_VALUE);
        if (char.class.equals(rawType) || Character.class.equals(rawType))
            return (char) readIntBetween(location, value, Character.MIN_VALUE, Character.MAX_VALUE);
        if (short.class.equals(rawType) || Short.class.equals(rawType))
            return (short) readIntBetween(location, value, Short.MIN_VALUE, Short.MAX_VALUE);
        if (int.class.equals(rawType) || Integer.class.equals(rawType))
            return value.intValueExact();
        if (long.class.equals(rawType) || Long.class.equals(rawType))
            return value.longValueExact();
        if (float.class.equals(rawType) || Float.class.equals(rawType))
            return (float) value.doubleValue();
        if (double.class.equals(rawType) || Double.class.equals(rawType))
            return value.doubleValue();
        if (BigInteger.class.equals(rawType))
            return value.bigIntegerValueExact();
        if (BigDecimal.class.equals(rawType))
            return value.bigDecimalValue();

        throw new GraphQlClientValueException(location, value);
    }

    private int readIntBetween(Location location, JsonNumber value, int minValue, int maxValue) {
        int intValue = value.intValueExact();
        check(location, value, intValue >= minValue);
        check(location, value, intValue <= maxValue);
        return intValue;
    }
}
