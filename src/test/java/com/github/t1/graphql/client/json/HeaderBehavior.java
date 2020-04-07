package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.api.GraphQlClientHeader;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.BDDAssertions.then;

class HeaderBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface StringApi {
        String greeting();
    }

    @Test void shouldAddCustomHeader() {
        Clock clock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
        fixture.returnsData("\"greeting\":\"dummy-greeting\"");
        StringApi api = fixture.builder()
            .header("H1", "V1")
            .header(new GraphQlClientHeader("H2", "V2"))
            .header(new GraphQlClientHeader("H3", clock::instant))
            .build(StringApi.class);

        String greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(fixture.sentHeader("H1")).isEqualTo("V1");
        then(fixture.sentHeader("H2")).isEqualTo("V2");
        then(fixture.sentHeader("H3")).isEqualTo(clock.instant());
        then(greeting).isEqualTo("dummy-greeting");
    }
}
