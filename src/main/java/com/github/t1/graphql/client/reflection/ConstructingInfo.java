package com.github.t1.graphql.client.reflection;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;

/** A static constructor method or a normal constructor */
@RequiredArgsConstructor
public class ConstructingInfo {
    private final Executable executable;

    @SneakyThrows(ReflectiveOperationException.class)
    public Object execute(Object... args) {
        if (executable instanceof Method) {
            return ((Method) executable).invoke(null, args);
        } else {
            return ((Constructor<?>) executable).newInstance(args);
        }
    }
}
