package com.github.t1.graphql.client;

import com.github.t1.graphql.client.api.GraphQlClientBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;

import javax.json.bind.Jsonb;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GraphQlClientFixture {
    private final Client mockClient = mock(Client.class);
    private final Invocation.Builder mockInvocationBuilder = mock(Invocation.Builder.class);
    private String configKey;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private URI endpoint = DUMMY_URI;
    private Response response;
    private Jsonb jsonb;

    public GraphQlClientFixture() {
        WebTarget mockWebTarget = mock(WebTarget.class);

        given(mockClient.target(DUMMY_URI)).willReturn(mockWebTarget);
        given(mockWebTarget.request(APPLICATION_JSON_TYPE)).willReturn(mockInvocationBuilder);
        given(mockInvocationBuilder.header(any(), any())).willReturn(mockInvocationBuilder);
        given(mockInvocationBuilder.post(any())).will(i -> response);
    }

    public void withHeader(String name, String value) {
        this.headers.put(name, value);
    }

    public void endpoint(URI endpoint) {
        this.endpoint = endpoint;
    }

    public void jsonb(Jsonb jsonb) {
        this.jsonb = jsonb;
    }

    public void configKey(String configKey) {
        this.configKey = configKey;
    }

    public <T> T buildClient(Class<T> apiClass) {
        GraphQlClientBuilder builder = GraphQlClientBuilder.newBuilder();
        if (endpoint != null)
            builder.endpoint(endpoint);
        headers.forEach(builder::header);
        builder.client(mockClient);
        if (jsonb != null)
            builder.jsonb(jsonb);
        if (configKey != null)
            builder.configKey(configKey);
        return builder.build(apiClass);
    }

    public void returnsData(String data) {
        this.response = Response.ok("{\"data\":{" + data + "}}").build();
    }

    public void returns(Response response) {
        this.response = response;
    }


    public String query() {
        return stripQueryContainer(captureRequestEntity());
    }

    private String captureRequestEntity() {
        @SuppressWarnings("unchecked") ArgumentCaptor<Entity<String>> captor = ArgumentCaptor.forClass(Entity.class);
        BDDMockito.then(mockInvocationBuilder).should().post(captor.capture());
        return captor.getValue().getEntity();
    }

    private String stripQueryContainer(String response) {
        then(response).startsWith(QUERY_PREFIX).endsWith(QUERY_SUFFIX);
        return response.substring(QUERY_PREFIX.length(), response.length() - QUERY_SUFFIX.length()).trim();
    }


    public String sentHeader(String name) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        BDDMockito.then(mockInvocationBuilder).should().header(eq(name), captor.capture());
        return captor.getValue();
    }


    private static final URI DUMMY_URI = URI.create("http://dummy-endpoint");
    private static final String QUERY_PREFIX = "{\"query\":\"{";
    private static final String QUERY_SUFFIX = "}\"}";
}
