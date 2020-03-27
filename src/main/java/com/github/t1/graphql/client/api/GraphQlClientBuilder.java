package com.github.t1.graphql.client.api;

import com.github.t1.graphql.client.impl.GraphQlClientBuilderImpl;

import javax.json.bind.Jsonb;
import javax.ws.rs.client.Client;
import java.net.URI;

public interface GraphQlClientBuilder {
    static GraphQlClientBuilder newBuilder() {
        // when the api is separated, we will use some resolver mechanism here
        return new GraphQlClientBuilderImpl();
    }

    GraphQlClientBuilder endpoint(String endpoint);

    GraphQlClientBuilder endpoint(URI endpoint);

    GraphQlClientBuilder client(Client client);

    GraphQlClientBuilder jsonb(Jsonb jsonb);

    <T> T build(Class<T> apiClass);
}
