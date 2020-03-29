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
        fixture.returnsData("\"greetings\":[\"a\",\"b\"]");
        StringSetApi api = fixture.buildClient(StringSetApi.class);

        Set<String> greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
    }


    interface StringListApi {
        List<String> greetings();
    }

    @Test void shouldCallStringListQuery() {
        fixture.returnsData("\"greetings\":[\"a\",\"b\"]");
        StringListApi api = fixture.buildClient(StringListApi.class);

        List<String> greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
    }


    interface StringArrayApi {
        String[] greetings();
    }

    @Test void shouldCallStringArrayQuery() {
        fixture.returnsData("\"greetings\":[\"a\",\"b\"]");
        StringArrayApi api = fixture.buildClient(StringArrayApi.class);

        String[] greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
    }


    interface OptionalStringApi {
        Optional<String> greeting();
    }

    @Test void shouldCallEmptyOptionalStringQuery() {
        fixture.returnsData("\"greeting\":[]");
        OptionalStringApi api = fixture.buildClient(OptionalStringApi.class);

        Optional<String> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).isEmpty();
    }

    @Test void shouldCallOptionalStringQuery() {
        fixture.returnsData("\"greeting\":[\"hi\"]");
        OptionalStringApi api = fixture.buildClient(OptionalStringApi.class);

        Optional<String> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).contains("hi");
    }

    @Test void shouldFailToCallOptionalStringQueryWithTwoValues() {
        fixture.returnsData("\"greeting\":[\"hi\",\"ho\"]");
        OptionalStringApi api = fixture.buildClient(OptionalStringApi.class);

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("more than one value in optional: [\"hi\",\"ho\"]");
    }


    interface OptionalGreetingApi {
        Optional<Greeting> greeting();
    }

    @Test void shouldCallOptionalGreetingQuery() {
        fixture.returnsData("\"greeting\":[{\"text\":\"hi\",\"code\":5}]");
        OptionalGreetingApi api = fixture.buildClient(OptionalGreetingApi.class);

        Optional<Greeting> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).contains(new Greeting("hi", 5));
    }

    @Test void shouldCallEmptyOptionalGreetingQuery() {
        fixture.returnsData("\"greeting\":[]");
        OptionalGreetingApi api = fixture.buildClient(OptionalGreetingApi.class);

        Optional<Greeting> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).isEmpty();
    }


    interface OptionalGreetingListApi {
        Optional<List<Greeting>> greeting();
    }

    @Test void shouldCallOptionalGreetingListQuery() {
        fixture.returnsData("\"greeting\":[[{\"text\":\"hi\",\"code\":5},{\"text\":\"ho\",\"code\":7}]]");
        OptionalGreetingListApi api = fixture.buildClient(OptionalGreetingListApi.class);

        Optional<List<Greeting>> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        assert greeting.isPresent();
        then(greeting.get()).contains(new Greeting("hi", 5), new Greeting("ho", 7));
    }

    @Test void shouldCallEmptyOptionalGreetingListQuery() {
        fixture.returnsData("\"greeting\":[]");
        OptionalGreetingListApi api = fixture.buildClient(OptionalGreetingListApi.class);

        Optional<List<Greeting>> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).isEmpty();
    }

    @Test void shouldCallOptionalEmptyGreetingListQuery() {
        fixture.returnsData("\"greeting\":[[]]");
        OptionalGreetingListApi api = fixture.buildClient(OptionalGreetingListApi.class);

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
        fixture.returnsData("\"greeting\":{\"text\":\"foo\",\"code\":5}");
        ObjectApi api = fixture.buildClient(ObjectApi.class);

        Greeting greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).isEqualTo(new Greeting("foo", 5));
    }


    interface ObjectListApi {
        List<Greeting> greetings();
    }

    @Test void shouldCallObjectListQuery() {
        fixture.returnsData("\"greetings\":[{\"text\":\"a\",\"code\":1},{\"text\":\"b\",\"code\":2}]");
        ObjectListApi api = fixture.buildClient(ObjectListApi.class);

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
        fixture.returnsData("\"container\":{\"greeting\":\"hi\",\"count\":5}");
        StringContainerApi api = fixture.buildClient(StringContainerApi.class);

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
        fixture.returnsData("\"container\":{\"greeting\":{\"text\":\"a\",\"code\":1},\"count\":3}");
        GreetingContainerApi api = fixture.buildClient(GreetingContainerApi.class);

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
        fixture.returnsData("\"container\":{\"greetings\":[" +
            "{\"text\":\"a\",\"code\":1},{\"text\":\"b\",\"code\":2}" +
            "],\"count\":3}");
        GreetingsContainerApi api = fixture.buildClient(GreetingsContainerApi.class);

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
        fixture.returnsData("\"container\":{\"greeting\":{" +
            "\"value\":{\"text\":\"a\",\"code\":1}}," +
            "\"count\":3}");
        WrappedGreetingApi api = fixture.buildClient(WrappedGreetingApi.class);

        WrappedGreetingContainer container = api.container();

        then(fixture.query()).isEqualTo("container {greeting{value{text code}} count}");
        then(container).isEqualTo(new WrappedGreetingContainer(
            new Wrapper<>(new Greeting("a", 1)), 3));
    }
}
