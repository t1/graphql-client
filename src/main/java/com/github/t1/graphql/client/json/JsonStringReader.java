package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.api.GraphQlClientException;
import com.github.t1.graphql.client.reflection.TypeInfo;

import javax.json.JsonString;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

class JsonStringReader extends Reader<JsonString> {
    JsonStringReader(TypeInfo type, Location location, JsonString value) { super(type, location, value); }

    @Override Object read() {
        if (char.class.equals(type.getRawType()) || Character.class.equals(type.getRawType())) {
            if (value.getChars().length() != 1)
                throw new GraphQlClientValueException(location, value);
            return value.getChars().charAt(0);
        }
        if (String.class.equals(type.getRawType()))
            return value.getString();
        if (type.isEnum())
            //noinspection rawtypes,unchecked
            return Enum.valueOf((Class) type.getRawType(), value.getString());
        Executable executable = type.scalarConstructor()
            .orElseThrow(() -> new GraphQlClientValueException(location, value));
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
