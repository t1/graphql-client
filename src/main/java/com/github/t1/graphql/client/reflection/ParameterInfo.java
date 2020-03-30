package com.github.t1.graphql.client.reflection;

import com.github.t1.graphql.client.api.GraphQlClientException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Parameter;

@EqualsAndHashCode
@RequiredArgsConstructor
public class ParameterInfo {
    private final MethodInfo method;
    private final Parameter parameter;
    @Getter private final Object value;

    @Override public String toString() { return "parameter '" + parameter.getName() + "' in " + method; }

    public String getName() {
        if (!parameter.isNamePresent())
            throw new GraphQlClientException("compile with -parameters to add the parameter names to the class file");
        return parameter.getName();
    }
}
