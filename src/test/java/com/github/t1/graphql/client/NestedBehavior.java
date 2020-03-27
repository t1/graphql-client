package com.github.t1.graphql.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.BDDAssertions.then;

public class NestedBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

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
}
