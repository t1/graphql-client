package com.github.t1.graphql.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.junit.jupiter.api.Test;

import javax.json.bind.annotation.JsonbProperty;

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

    interface ObjectApi {
        Greeting greeting();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class Greeting {
        @JsonbProperty("foo") String text;
        @JsonbProperty("key") int code;
    }

    @Test void shouldCallObjectQuery() {
        fixture.returnsData("\"greeting\":{\"foo\":\"foo\",\"key\":5}");
        ObjectApi api = fixture.buildClient(ObjectApi.class);

        Greeting greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {foo key}");
        then(greeting).isEqualTo(new Greeting("foo", 5));
    }
}
