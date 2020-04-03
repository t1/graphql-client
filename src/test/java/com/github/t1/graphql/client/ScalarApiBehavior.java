package com.github.t1.graphql.client;

import com.github.t1.graphql.client.api.GraphQlClientException;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.time.LocalDate;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

class ScalarApiBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();


    interface IntegerApi {
        Integer code();
    }

    @Test void shouldCallIntegerQuery() {
        fixture.returnsData("\"code\":5");
        IntegerApi api = fixture.builder().build(IntegerApi.class);

        Integer code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo(5);
    }


    interface IntApi {
        int code();
    }

    @Test void shouldCallIntQuery() {
        fixture.returnsData("\"code\":5");
        IntApi api = fixture.builder().build(IntApi.class);

        int code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo(5);
    }


    interface BoolApi {
        boolean bool();
    }

    @Test void shouldCallBoolQuery() {
        fixture.returnsData("\"bool\":true");
        BoolApi api = fixture.builder().build(BoolApi.class);

        boolean bool = api.bool();

        then(fixture.query()).isEqualTo("bool");
        then(bool).isTrue();
    }


    interface BooleanApi {
        Boolean bool();
    }

    @Test void shouldCallBooleanQuery() {
        fixture.returnsData("\"bool\":true");
        BooleanApi api = fixture.builder().build(BooleanApi.class);

        Boolean bool = api.bool();

        then(fixture.query()).isEqualTo("bool");
        then(bool).isTrue();
    }


    interface DoubleApi {
        Double number();
    }

    @Test void shouldCallDoubleQuery() {
        fixture.returnsData("\"number\":123.456");
        DoubleApi api = fixture.builder().build(DoubleApi.class);

        Double number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(123.456D);
    }


    interface PrimitiveDoubleApi {
        double number();
    }

    @Test void shouldCallPrimitiveDoubleQuery() {
        fixture.returnsData("\"number\":123.456");
        PrimitiveDoubleApi api = fixture.builder().build(PrimitiveDoubleApi.class);

        double number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(123.456D);
    }


    interface StringApi {
        String greeting();
    }

    @Test void shouldCallStringQuery() {
        fixture.returnsData("\"greeting\":\"dummy-greeting\"");
        StringApi api = fixture.builder().build(StringApi.class);

        String greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).isEqualTo("dummy-greeting");
    }

    @Test void shouldFailStringQueryNotFound() {
        fixture.returns(Response.serverError().type(TEXT_PLAIN_TYPE).entity("failed").build());
        StringApi api = fixture.builder().build(StringApi.class);

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("expected successful status code but got 500 Internal Server Error:\n" +
            "failed");
    }

    @Test void shouldFailOnQueryError() {
        fixture.returns(Response.ok("{\"errors\":[{\"message\":\"failed\"}]}").build());
        StringApi api = fixture.builder().build(StringApi.class);

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("errors from service: [{\"message\":\"failed\"}]:\n" +
            "  {\"query\":\"{ greeting }\"}");
    }

    @Test void shouldFailOnMissingQueryResponse() {
        fixture.returnsData("");
        StringApi api = fixture.builder().build(StringApi.class);

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("no data for 'greeting':\n  {}");
    }


    interface ScalarWithValueOfApi {
        Integer foo();
    }

    @Test void shouldCallScalarWithValueOfQuery() {
        fixture.returnsData("\"foo\":123456");
        ScalarWithValueOfApi api = fixture.builder().build(ScalarWithValueOfApi.class);

        Integer value = api.foo();

        then(fixture.query()).isEqualTo("foo");
        then(value).isEqualTo(123456);
    }


    interface ScalarWithParseApi {
        LocalDate now();
    }

    @Test void shouldCallScalarWithParseQuery() {
        LocalDate now = LocalDate.now();
        fixture.returnsData("\"now\":\"" + now + "\"");
        ScalarWithParseApi api = fixture.builder().build(ScalarWithParseApi.class);

        LocalDate value = api.now();

        then(fixture.query()).isEqualTo("now");
        then(value).isEqualTo(now);
    }


    interface ScalarWithStringConstructorApi {
        BigInteger foo();
    }

    @Test void shouldCallScalarWithStringConstructorApiQuery() {
        String bigNumber = "1234567890123456789012345678901234567890123456789012345678901234567890";
        fixture.returnsData("\"foo\":" + bigNumber);
        ScalarWithStringConstructorApi api = fixture.builder().build(ScalarWithStringConstructorApi.class);

        BigInteger value = api.foo();

        then(fixture.query()).isEqualTo("foo");
        then(value).isEqualTo(bigNumber);
    }
}
