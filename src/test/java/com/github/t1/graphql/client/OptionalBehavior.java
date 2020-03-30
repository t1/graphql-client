package com.github.t1.graphql.client;

import com.github.t1.graphql.client.NestedBehavior.Greeting;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.BDDAssertions.then;

public class OptionalBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();


    interface OptionalStringApi {
        Optional<String> greeting();
    }

    @Test void shouldCallNullOptionalStringQuery() {
        fixture.returnsData("\"greeting\":null");
        OptionalStringApi api = fixture.buildClient(OptionalStringApi.class);

        Optional<String> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).isEmpty();
    }

    @Test void shouldCallOptionalStringQuery() {
        fixture.returnsData("\"greeting\":\"hi\"");
        OptionalStringApi api = fixture.buildClient(OptionalStringApi.class);

        Optional<String> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).contains("hi");
    }


    interface OptionalGreetingApi {
        Optional<Greeting> greeting();
    }

    @Test void shouldCallOptionalGreetingQuery() {
        fixture.returnsData("\"greeting\":{\"text\":\"hi\",\"code\":5}");
        OptionalGreetingApi api = fixture.buildClient(OptionalGreetingApi.class);

        Optional<Greeting> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).contains(new Greeting("hi", 5));
    }

    @Test void shouldCallNullOptionalGreetingQuery() {
        fixture.returnsData("\"greeting\":null");
        OptionalGreetingApi api = fixture.buildClient(OptionalGreetingApi.class);

        Optional<Greeting> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).isEmpty();
    }


    interface OptionalGreetingListApi {
        Optional<List<Greeting>> greeting();
    }

    @Test void shouldCallOptionalGreetingListQuery() {
        fixture.returnsData("\"greeting\":[{\"text\":\"hi\",\"code\":5},{\"text\":\"ho\",\"code\":7}]");
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
        assert greeting.isPresent();
        then(greeting.get()).isEmpty();
    }


    interface ListOfOptionalGreetingApi {
        List<Optional<Greeting>> greetings();
    }

    @Test void shouldCallListOfOptionalGreetingsQuery() {
        fixture.returnsData("\"greetings\":[{\"text\":\"hi\",\"code\":5},{\"text\":\"ho\",\"code\":7}]");
        ListOfOptionalGreetingApi api = fixture.buildClient(ListOfOptionalGreetingApi.class);

        List<Optional<Greeting>> greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings {text code}");
        then(greetings).containsExactly(
            Optional.of(new Greeting("hi", 5)),
            Optional.of(new Greeting("ho", 7)));
    }

    @Test void shouldCallEmptyListOfOptionalGreetingsQuery() {
        fixture.returnsData("\"greetings\":[]");
        ListOfOptionalGreetingApi api = fixture.buildClient(ListOfOptionalGreetingApi.class);

        List<Optional<Greeting>> greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings {text code}");
        then(greetings).isEmpty();
    }


    interface OptionalOptionalStringApi {
        Optional<Optional<String>> greeting();
    }

    @Test void shouldCallNullOptionalOptionalStringQuery() {
        fixture.returnsData("\"greeting\":null");
        OptionalOptionalStringApi api = fixture.buildClient(OptionalOptionalStringApi.class);

        Optional<Optional<String>> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).isEmpty();
    }

    @Test void shouldCallOptionalOptionalStringQuery() {
        fixture.returnsData("\"greeting\":\"hi\"");
        OptionalOptionalStringApi api = fixture.buildClient(OptionalOptionalStringApi.class);

        Optional<Optional<String>> greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).contains(Optional.of("hi"));
    }
}
