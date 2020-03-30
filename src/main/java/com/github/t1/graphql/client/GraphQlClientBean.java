package com.github.t1.graphql.client;

import com.github.t1.graphql.client.api.GraphQlClientBuilder;
import com.github.t1.graphql.client.cdi.AbstractBean;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.spi.CreationalContext;

@Slf4j
public class GraphQlClientBean<T> extends AbstractBean<T> {
    public GraphQlClientBean(Class<T> apiClass) { super(apiClass); }

    @Override public T create(CreationalContext<T> creationalContext) {
        log.debug("create GraphQL Client proxy: {}", type);
        return GraphQlClientBuilder.newBuilder().build(type);
    }
}
