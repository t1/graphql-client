package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.api.GraphQlClientException;
import com.github.t1.graphql.client.reflection.TypeInfo;
import lombok.RequiredArgsConstructor;

import javax.json.JsonString;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
class JsonStringReader implements Supplier<Object> {
    private final TypeInfo type;
    private final JsonString value;

    @Override public Object get() {
        if (char.class.equals(type.getRawType()) || Character.class.equals(type.getRawType())) {
            if (value.getChars().length() != 1)
                throw new GraphQlClientException("invalid value for " + type.getRawType().getName()
                    + " field " + "code" /* TODO field name */ + ": '" + value.getString() + "'");
            return value.getChars().charAt(0);
        }
        if (String.class.equals(type.getRawType()))
            return value.getString();
        if (type.isEnum())
            //noinspection rawtypes,unchecked
            return Enum.valueOf((Class) type.getRawType(), value.getString());
        Executable executable = type.scalarConstructor().orElseThrow(() -> new GraphQlClientException("expected a scalar constructor on " + type));
        return execute(executable, value.getString());
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
}
