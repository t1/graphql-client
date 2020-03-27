package com.github.t1.graphql.client;

import com.github.t1.graphql.client.api.GraphQlClientException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

class NestedBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface StringSetApi {
        Set<String> greetings();
    }

    @Test void shouldCallStringSetQuery() {
        StringSetApi api = fixture.buildClient(StringSetApi.class);
        fixture.returnsData("\"greetings\":[\"a\",\"b\"]");

        Set<String> greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
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


    interface StringArrayApi {
        String[] greetings();
    }

    @Test void shouldCallStringArrayQuery() {
        StringArrayApi api = fixture.buildClient(StringArrayApi.class);
        fixture.returnsData("\"greetings\":[\"a\",\"b\"]");

        String[] greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
    }


    interface OptionalStringApi {
        Optional<String> greeting();
    }

    @Test void shouldCallEmptyOptionalStringQuery() {
        OptionalStringApi api = fixture.buildClient(OptionalStringApi.class);
        fixture.returnsData("\"greeting\":[]");

        Optional<String> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).isEmpty();
    }

    @Test void shouldCallOptionalStringQuery() {
        OptionalStringApi api = fixture.buildClient(OptionalStringApi.class);
        fixture.returnsData("\"greeting\":[\"hi\"]");

        Optional<String> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).contains("hi");
    }

    @Test void shouldFailToCallOptionalStringQueryWithTwoValues() {
        OptionalStringApi api = fixture.buildClient(OptionalStringApi.class);
        fixture.returnsData("\"greeting\":[\"hi\",\"ho\"]");

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("more than one value in optional: [\"hi\",\"ho\"]");
    }


    interface OptionalGreetingApi {
        Optional<Greeting> greeting();
    }

    @Test void shouldCallOptionalGreetingQuery() {
        OptionalGreetingApi api = fixture.buildClient(OptionalGreetingApi.class);
        fixture.returnsData("\"greeting\":[{\"text\":\"hi\",\"code\":5}]");

        Optional<Greeting> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).contains(new Greeting("hi", 5));
    }

    @Test void shouldCallEmptyOptionalGreetingQuery() {
        OptionalGreetingApi api = fixture.buildClient(OptionalGreetingApi.class);
        fixture.returnsData("\"greeting\":[]");

        Optional<Greeting> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).isEmpty();
    }


    interface OptionalGreetingListApi {
        Optional<List<Greeting>> greeting();
    }

    @Test void shouldCallOptionalGreetingListQuery() {
        OptionalGreetingListApi api = fixture.buildClient(OptionalGreetingListApi.class);
        fixture.returnsData("\"greeting\":[[{\"text\":\"hi\",\"code\":5},{\"text\":\"ho\",\"code\":7}]]");

        Optional<List<Greeting>> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        assert greeting.isPresent();
        then(greeting.get()).contains(new Greeting("hi", 5), new Greeting("ho", 7));
    }

    @Test void shouldCallEmptyOptionalGreetingListQuery() {
        OptionalGreetingListApi api = fixture.buildClient(OptionalGreetingListApi.class);
        fixture.returnsData("\"greeting\":[]");

        Optional<List<Greeting>> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).isEmpty();
    }

    @Test void shouldCallOptionalEmptyGreetingListQuery() {
        OptionalGreetingListApi api = fixture.buildClient(OptionalGreetingListApi.class);
        fixture.returnsData("\"greeting\":[[]]");

        Optional<List<Greeting>> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        assert greeting.isPresent();
        then(greeting.get()).isEmpty();
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
