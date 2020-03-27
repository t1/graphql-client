package com.github.t1.graphql.client.impl;

import com.github.t1.graphql.client.api.GraphQlClientApi;
import com.github.t1.graphql.client.api.GraphQlClientBuilder;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.spi.CreationalContext;

@Slf4j
public class GraphQlClientBean<T> extends AbstractBean<T> {
    private final GraphQlClientApi config;

    public GraphQlClientBean(Class<T> apiClass, GraphQlClientApi config) {
        super(apiClass);
        this.config = config;
    }

    @Override public T create(CreationalContext<T> creationalContext) {
        log.debug("create GraphQL Client proxy: {} :: {}", type, config);
        GraphQlClientBuilder builder = GraphQlClientBuilder.newBuilder();
        if (!config.endpoint().isEmpty())
            builder.endpoint(config.endpoint());
        return builder.build(type);
    }
}
