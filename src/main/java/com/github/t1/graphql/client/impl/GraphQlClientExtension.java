package com.github.t1.graphql.client.impl;

import com.github.t1.graphql.client.api.GraphQlClientApi;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
public class GraphQlClientExtension implements Extension {
    private final Set<RuntimeException> errors = new LinkedHashSet<>();
    private final Map<Class<?>, GraphQlClientApi> apis = new LinkedHashMap<>();

    public void registerGraphQlClientApis(@Observes @WithAnnotations(GraphQlClientApi.class) ProcessAnnotatedType<?> type) {
        Class<?> javaClass = type.getAnnotatedType().getJavaClass();
        if (javaClass.isInterface()) {
            log.info("register {}", javaClass.getName());
            GraphQlClientApi annotation = type.getAnnotatedType().getAnnotation(GraphQlClientApi.class);
            apis.put(javaClass, annotation);
        } else {
            errors.add(new IllegalArgumentException("Rest client needs to be an interface " + javaClass));
        }
    }

    public void reportErrors(@Observes AfterDeploymentValidation afterDeploymentValidation) {
        for (RuntimeException error : errors) {
            afterDeploymentValidation.addDeploymentProblem(error);
        }
    }

    public void createProxies(@Observes AfterBeanDiscovery afterBeanDiscovery) {
        for (Map.Entry<Class<?>, GraphQlClientApi> api : apis.entrySet()) {
            afterBeanDiscovery.addBean(new GraphQlClientBean<>(api.getKey(), api.getValue()));
        }
    }
}
