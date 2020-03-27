package com.github.t1.graphql.client;

import com.github.t1.graphql.client.api.GraphQlClientException;
import org.eclipse.microprofile.graphql.Query;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

public class ScalarApiBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();


    interface IntegerApi {
        Integer code();
    }

    @Test void shouldCallIntegerQuery() {
        IntegerApi api = fixture.buildClient(IntegerApi.class);
        fixture.returnsData("\"code\":5");

        Integer code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo(5);
    }


    interface IntApi {
        int code();
    }

    @Test void shouldCallIntQuery() {
        IntApi api = fixture.buildClient(IntApi.class);
        fixture.returnsData("\"code\":5");

        int code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo(5);
    }


    interface BoolApi {
        boolean bool();
    }

    @Test void shouldCallBoolQuery() {
        BoolApi api = fixture.buildClient(BoolApi.class);
        fixture.returnsData("\"bool\":true");

        boolean bool = api.bool();

        then(fixture.query()).isEqualTo("bool");
        then(bool).isTrue();
    }


    interface BooleanApi {
        Boolean bool();
    }

    @Test void shouldCallBooleanQuery() {
        BooleanApi api = fixture.buildClient(BooleanApi.class);
        fixture.returnsData("\"bool\":true");

        Boolean bool = api.bool();

        then(fixture.query()).isEqualTo("bool");
        then(bool).isTrue();
    }


    interface DoubleApi {
        Double number();
    }

    @Test void shouldCallDoubleQuery() {
        DoubleApi api = fixture.buildClient(DoubleApi.class);
        fixture.returnsData("\"number\":123.456");

        Double number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(123.456D);
    }


    interface PrimitiveDoubleApi {
        double number();
    }

    @Test void shouldCallPrimitiveDoubleQuery() {
        PrimitiveDoubleApi api = fixture.buildClient(PrimitiveDoubleApi.class);
        fixture.returnsData("\"number\":123.456");

        double number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(123.456D);
    }


    interface StringApi {
        String greeting();
    }

    @Test void shouldCallStringQuery() {
        StringApi api = fixture.buildClient(StringApi.class);
        fixture.returnsData("\"greeting\":\"dummy-greeting\"");

        String greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).isEqualTo("dummy-greeting");
    }

    @Test void shouldFailStringQueryNotFound() {
        StringApi api = fixture.buildClient(StringApi.class);
        fixture.returns(Response.serverError().type(TEXT_PLAIN_TYPE).entity("failed").build());

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("expected successful status code but got 500 Internal Server Error:\n" +
            "failed");
    }

    @Test void shouldFailOnQueryError() {
        StringApi api = fixture.buildClient(StringApi.class);
        fixture.returns(Response.ok("{\"errors\":[{\"message\":\"failed\"}]}").build());

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("GraphQL error: [{\"message\":\"failed\"}]:\n" +
            "  {\"query\":\"{ greeting }\"}");
    }


    interface RenamedStringApi {
        @Query("greeting") String foo();
    }

    @Test void shouldCallRenamedStringQuery() {
        RenamedStringApi api = fixture.buildClient(RenamedStringApi.class);
        fixture.returnsData("\"greeting\":\"dummy-greeting\"");

        String greeting = api.foo();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).isEqualTo("dummy-greeting");
    }
}
