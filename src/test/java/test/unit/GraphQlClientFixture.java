package test.unit;

import com.github.t1.graphql.client.api.GraphQlClientBuilder;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class GraphQlClientFixture {
    private final Client mockClient = mock(Client.class);
    private final Invocation.Builder mockInvocationBuilder = mock(Invocation.Builder.class);
    private Response response;

    public GraphQlClientFixture() {
        WebTarget mockWebTarget = mock(WebTarget.class);

        given(mockClient.target(any(URI.class))).willReturn(mockWebTarget);
        given(mockWebTarget.request(APPLICATION_JSON_TYPE)).willReturn(mockInvocationBuilder);
        given(mockInvocationBuilder.headers(any())).willReturn(mockInvocationBuilder);
        given(mockInvocationBuilder.post(any())).will(i -> response);
    }

    public GraphQlClientBuilder builder() {
        return builderWithoutEndpointConfig().endpoint("urn:dummy-endpoint");
    }

    public GraphQlClientBuilder builderWithoutEndpointConfig() {
        return GraphQlClientBuilder.newBuilder().client(mockClient);
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


    public Object sentHeader(String name) {
        return sentHeaders().getFirst(name);
    }

    public URI endpointUsed() {
        ArgumentCaptor<URI> captor = ArgumentCaptor.forClass(URI.class);
        BDDMockito.then(mockClient).should().target(captor.capture());
        return captor.getValue();
    }

    public MultivaluedMap<String, Object> sentHeaders() {
        @SuppressWarnings("unchecked") ArgumentCaptor<MultivaluedMap<String, Object>> captor = ArgumentCaptor.forClass(MultivaluedMap.class);
        BDDMockito.then(mockInvocationBuilder).should().headers(captor.capture());
        return captor.getValue();
    }

    private static final String QUERY_PREFIX = "{\"query\":\"{";
    private static final String QUERY_SUFFIX = "}\"}";
}
