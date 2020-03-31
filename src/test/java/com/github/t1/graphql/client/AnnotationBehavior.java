package com.github.t1.graphql.client;

import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

public class AnnotationBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface RenamedStringApi {
        @Query("greeting") String foo();
    }

    @Test void shouldCallRenamedStringQuery() {
        fixture.returnsData("\"greeting\":\"dummy-greeting\"");
        RenamedStringApi api = fixture.buildClient(RenamedStringApi.class);

        String greeting = api.foo();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).isEqualTo("dummy-greeting");
    }


    interface RenamedParamApi {
        String greeting(@Name("who") String foo);
    }

    @Test void shouldCallParamQuery() {
        fixture.returnsData("\"greeting\":\"hi, foo\"");
        RenamedParamApi api = fixture.buildClient(RenamedParamApi.class);

        String greeting = api.greeting("foo");

        then(fixture.query()).isEqualTo("greeting(who: \\\"foo\\\")");
        then(greeting).isEqualTo("hi, foo");
    }
}
