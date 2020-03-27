package com.github.t1.graphql.client.impl;

import com.github.t1.graphql.client.api.GraphQlClientBuilder;
import com.github.t1.graphql.client.impl.reflection.MethodInfo;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.lang.reflect.Proxy;
import java.net.URI;

public class GraphQlClientBuilderImpl implements GraphQlClientBuilder {
    private URI endpoint;
    private Client client = DEFAULT_CLIENT;
    private Jsonb jsonb = DEFAULT_JSONB;

    @Override public GraphQlClientBuilder endpoint(String endpoint) {
        return endpoint(URI.create(endpoint));
    }

    @Override public GraphQlClientBuilder endpoint(URI endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    @Override public GraphQlClientBuilder client(Client client) {
        this.client = client;
        return this;
    }

    @Override public GraphQlClientBuilder jsonb(Jsonb jsonb) {
        this.jsonb = jsonb;
        return this;
    }

    @Override public <T> T build(Class<T> apiClass) {
        // TODO default endpoint from MP Config
        WebTarget webTarget = client.target(endpoint);
        GraphQlClientImpl graphQlClient = new GraphQlClientImpl(webTarget, jsonb);
        return apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), new Class<?>[]{apiClass},
            (proxy, method, args) -> graphQlClient.invoke(MethodInfo.of(method, args))));
    }

    private static final Client DEFAULT_CLIENT = ClientBuilder.newClient();

    private static final Jsonb DEFAULT_JSONB = JsonbBuilder.create();
}
