package com.github.t1.graphql.client;

import com.github.t1.graphql.client.api.GraphQlClientApi;
import com.github.t1.graphql.client.api.GraphQlClientBuilder;
import com.github.t1.graphql.client.reflection.MethodInfo;
import org.eclipse.microprofile.config.ConfigProvider;

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
    private String configKey = null;

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

    @Override public GraphQlClientBuilder configKey(String configKey) {
        this.configKey = configKey;
        return this;
    }

    @Override public <T> T build(Class<T> apiClass) {
        readConfig(apiClass.getAnnotation(GraphQlClientApi.class));

        WebTarget webTarget = client.target(resolveEndpoint(apiClass));
        GraphQlClientProxy graphQlClient = new GraphQlClientProxy(webTarget, jsonb);
        return apiClass.cast(Proxy.newProxyInstance(apiClass.getClassLoader(), new Class<?>[]{apiClass},
            (proxy, method, args) -> graphQlClient.invoke(MethodInfo.of(method, args))));
    }

    private void readConfig(GraphQlClientApi config) {
        if (config == null)
            return;
        if (this.endpoint == null && !config.endpoint().isEmpty())
            this.endpoint = URI.create(config.endpoint());
        if (this.configKey == null && !config.configKey().isEmpty())
            this.configKey = config.configKey();
    }

    private URI resolveEndpoint(Class<?> apiClass) {
        if (endpoint != null)
            return endpoint;
        return ConfigProvider.getConfig().getValue(configKey(apiClass) + "/mp-graphql/url", URI.class);
    }

    private String configKey(Class<?> apiClass) {
        return (configKey == null) ? apiClass.getName() : configKey;
    }

    private static final Client DEFAULT_CLIENT = ClientBuilder.newClient();

    private static final Jsonb DEFAULT_JSONB = JsonbBuilder.create();
}
