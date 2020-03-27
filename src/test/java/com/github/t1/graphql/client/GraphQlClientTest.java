package com.github.t1.graphql.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.graphql.Query;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

public class GraphQlClientTest {

    private final GraphQlClientFixture fixture = new GraphQlClientFixture();


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


    interface StringListApi {
        List<String> greetings();
    }

    @Test void shouldCallStringListQuery() {
        StringListApi api = fixture.buildClient(StringListApi.class);
        fixture.returnsData("\"greetings\":[\"a\",\"b\"]");

        List<String> greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
    }


    interface ObjectApi {
        Greeting greeting();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class Greeting {
        String text;
        int code;
    }

    @Test void shouldCallObjectQuery() {
        ObjectApi api = fixture.buildClient(ObjectApi.class);
        fixture.returnsData("\"greeting\":{\"text\":\"foo\",\"code\":5}");

        Greeting greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).isEqualTo(new Greeting("foo", 5));
    }


    interface ObjectListApi {
        List<Greeting> greetings();
    }

    @Test void shouldCallObjectListQuery() {
        ObjectListApi api = fixture.buildClient(ObjectListApi.class);
        fixture.returnsData("\"greetings\":[{\"text\":\"a\",\"code\":1},{\"text\":\"b\",\"code\":2}]");

        List<Greeting> greeting = api.greetings();

        then(fixture.query()).isEqualTo("greetings {text code}");
        then(greeting).containsExactly(
            new Greeting("a", 1),
            new Greeting("b", 2));
    }


    interface StringContainerApi {
        StringContainer container();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class StringContainer {
        String greeting;
        int count;
    }

    @Test void shouldCallNestedStringQuery() {
        StringContainerApi api = fixture.buildClient(StringContainerApi.class);
        fixture.returnsData("\"container\":{\"greeting\":\"hi\",\"count\":5}");

        StringContainer container = api.container();

        then(fixture.query()).isEqualTo("container {greeting count}");
        then(container).isEqualTo(new StringContainer("hi", 5));
    }


    interface GreetingContainerApi {
        GreetingContainer container();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class GreetingContainer {
        Greeting greeting;
        int count;
    }

    @Test void shouldCallNestedObjectQuery() {
        GreetingContainerApi api = fixture.buildClient(GreetingContainerApi.class);
        fixture.returnsData("\"container\":{\"greeting\":{\"text\":\"a\",\"code\":1},\"count\":3}");

        GreetingContainer container = api.container();

        then(fixture.query()).isEqualTo("container {greeting{text code} count}");
        then(container).isEqualTo(new GreetingContainer(
            new Greeting("a", 1), 3));
    }


    interface GreetingsContainerApi {
        GreetingsContainer container();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class GreetingsContainer {
        List<Greeting> greetings;
        int count;
    }

    @Test void shouldCallNestedListQuery() {
        GreetingsContainerApi api = fixture.buildClient(GreetingsContainerApi.class);
        fixture.returnsData("\"container\":{\"greetings\":[" +
            "{\"text\":\"a\",\"code\":1},{\"text\":\"b\",\"code\":2}" +
            "],\"count\":3}");

        GreetingsContainer container = api.container();

        then(fixture.query()).isEqualTo("container {greetings{text code} count}");
        then(container).isEqualTo(new GreetingsContainer(
            asList(new Greeting("a", 1), new Greeting("b", 2)), 3));
    }

    // TODO Optionals, Arrays and more

    interface WrappedGreetingApi {
        WrappedGreetingContainer container();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class WrappedGreetingContainer {
        Wrapper<Greeting> greeting;
        int count;
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class Wrapper<T> {
        private T value;
    }

    @Test void shouldCallWrappedGreetingQuery() {
        WrappedGreetingApi api = fixture.buildClient(WrappedGreetingApi.class);
        fixture.returnsData("\"container\":{\"greeting\":{" +
            "\"value\":{\"text\":\"a\",\"code\":1}}," +
            "\"count\":3}");

        WrappedGreetingContainer container = api.container();

        then(fixture.query()).isEqualTo("container {greeting{value{text code}} count}");
        then(container).isEqualTo(new WrappedGreetingContainer(
            new Wrapper<>(new Greeting("a", 1)), 3));
    }


    interface ParamApi {
        String greeting(String who);
    }

    @Test void shouldCallParamQuery() {
        ParamApi api = fixture.buildClient(ParamApi.class);
        fixture.returnsData("\"greeting\":\"hi, foo\"");

        String greeting = api.greeting("foo");

        then(fixture.query()).isEqualTo("greeting(who: \\\"foo\\\")");
        then(greeting).isEqualTo("hi, foo");
    }


    interface ParamsApi {
        String greeting(String who, int count);
    }

    @Test void shouldCallTwoParamsQuery() {
        ParamsApi api = fixture.buildClient(ParamsApi.class);
        fixture.returnsData("\"greeting\":\"hi, foo 3\"");

        String greeting = api.greeting("foo", 3);

        then(fixture.query()).isEqualTo("greeting(who: \\\"foo\\\", count: 3)");
        then(greeting).isEqualTo("hi, foo 3");
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
