package com.github.t1.graphql.client.reflection;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.graphql.Query;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode
@RequiredArgsConstructor(access = PRIVATE)
public class MethodInfo {
    public static MethodInfo of(Method method, Object... args) {
        return new MethodInfo(new TypeInfo(null, method.getDeclaringClass()), method, args);
    }

    private final TypeInfo type;
    private final Method method;
    private final Object[] parameterValues;

    @Override public String toString() { return "method '" + method.getName() + "' in " + type; }

    public String getName() {
        if (method.isAnnotationPresent(Query.class)) {
            Query query = method.getAnnotation(Query.class);
            if (!query.value().isEmpty()) {
                return query.value();
            }
        }
        return method.getName();
    }

    public int getParameterCount() { return method.getParameterCount(); }

    public TypeInfo getReturnType() { return new TypeInfo(type, method.getGenericReturnType()); }

    public List<ParameterInfo> getParameters() {
        Parameter[] parameters = method.getParameters();
        assert parameters.length == parameterValues.length;
        List<ParameterInfo> list = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            list.add(new ParameterInfo(this, parameters[i], parameterValues[i]));
        }
        return list;
    }
}