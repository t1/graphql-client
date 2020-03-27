package com.github.t1.graphql.client.impl;

import com.github.t1.graphql.client.api.GraphQlClientApi;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class GraphQlClientExtension implements Extension {
    private final Set<RuntimeException> errors = new LinkedHashSet<>();
    private final List<Class<?>> apis = new ArrayList<>();

    public void registerGraphQlClientApis(@Observes @WithAnnotations(GraphQlClientApi.class) ProcessAnnotatedType<?> type) {
        Class<?> javaClass = type.getAnnotatedType().getJavaClass();
        if (javaClass.isInterface()) {
            log.info("register {}", javaClass.getName());
            apis.add(javaClass);
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
        for (Class<?> api : apis) {
            afterBeanDiscovery.addBean(new GraphQlClientBean<>(api));
        }
    }
}
