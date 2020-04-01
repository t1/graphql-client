package com.github.t1.graphql.client.api;

import com.github.t1.graphql.client.GraphQlClientBuilderImpl;

import javax.json.bind.Jsonb;
import javax.ws.rs.client.Client;
import java.net.URI;

public interface GraphQlClientBuilder {
    static GraphQlClientBuilder newBuilder() { return new GraphQlClientBuilderImpl(); }

    GraphQlClientBuilder configKey(String configKey);

    GraphQlClientBuilder client(Client client);

    default GraphQlClientBuilder endpoint(String endpoint) { return endpoint(URI.create(endpoint)); }

    GraphQlClientBuilder endpoint(URI endpoint);

    GraphQlClientBuilder header(String name, String value);

    GraphQlClientBuilder jsonb(Jsonb jsonb);

    <T> T build(Class<T> apiClass);
}
