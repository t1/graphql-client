package com.github.t1.graphql.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class HeaderBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface StringApi {
        String greeting();
    }

    @Test void shouldAddCustomHeader() {
        fixture.returnsData("\"greeting\":\"dummy-greeting\"");
        fixture.withHeader("Foo-Header", "Bar");
        StringApi api = fixture.buildClient(StringApi.class);

        String greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(fixture.sentHeader("Foo-Header")).isEqualTo("Bar");
        then(greeting).isEqualTo("dummy-greeting");
    }
}
