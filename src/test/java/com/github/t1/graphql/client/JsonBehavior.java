package com.github.t1.graphql.client;

import org.junit.jupiter.api.Test;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.time.LocalDate;

import static javax.json.bind.JsonbConfig.DATE_FORMAT;
import static org.assertj.core.api.BDDAssertions.then;

public class JsonBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface Api {
        LocalDate foo();
    }

    @Test void shouldConfigureJsonb() {
        fixture.jsonb(JsonbBuilder.create(new JsonbConfig().setProperty(DATE_FORMAT, "dd.MM.yyyy")));
        fixture.returnsData("\"foo\":\"27.03.2020\"");
        Api api = fixture.buildClient(Api.class);

        LocalDate foo = api.foo();

        then(fixture.query()).isEqualTo("foo");
        then(foo).isEqualTo(LocalDate.of(2020, 3, 27));
    }
}
