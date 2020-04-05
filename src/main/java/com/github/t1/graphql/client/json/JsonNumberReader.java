package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;

import javax.json.JsonNumber;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Supplier;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
class JsonNumberReader implements Supplier<Object> {
    private final TypeInfo type;
    private final JsonNumber value;

    @Override public Object get() {
        if (byte.class.equals(type.getRawType()) || Byte.class.equals(type.getRawType()))
            return (byte) value.intValue();
        if (short.class.equals(type.getRawType()) || Short.class.equals(type.getRawType()))
            return (short) value.intValue();
        if (int.class.equals(type.getRawType()) || Integer.class.equals(type.getRawType()))
            return value.intValueExact();
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
        return null;
    }
}