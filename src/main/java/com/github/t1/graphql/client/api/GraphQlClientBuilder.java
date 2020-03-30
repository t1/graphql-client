package com.github.t1.graphql.client.api;

import com.github.t1.graphql.client.GraphQlClientBuilderImpl;

import javax.json.bind.Jsonb;
import javax.ws.rs.client.Client;
import java.net.URI;

public interface GraphQlClientBuilder {
    static GraphQlClientBuilder newBuilder() { return new GraphQlClientBuilderImpl(); }

    GraphQlClientBuilder endpoint(String endpoint);

    GraphQlClientBuilder endpoint(URI endpoint);

    GraphQlClientBuilder client(Client client);

    GraphQlClientBuilder jsonb(Jsonb jsonb);

    GraphQlClientBuilder configKey(String configKey);

    <T> T build(Class<T> apiClass);
}
