package com.github.t1.graphql.client.json;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class ParametersBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface ParamApi {
        String greeting(String who);
    }

    @Test void shouldCallParamQuery() {
        fixture.returnsData("\"greeting\":\"hi, foo\"");
        ParamApi api = fixture.builder().build(ParamApi.class);

        String greeting = api.greeting("foo");

        then(fixture.query()).isEqualTo("greeting(who: \\\"foo\\\")");
        then(greeting).isEqualTo("hi, foo");
    }


    interface ParamsApi {
        String greeting(String who, int count);
    }

    @Test void shouldCallTwoParamsQuery() {
        fixture.returnsData("\"greeting\":\"hi, foo 3\"");
        ParamsApi api = fixture.builder().build(ParamsApi.class);

        String greeting = api.greeting("foo", 3);

        then(fixture.query()).isEqualTo("greeting(who: \\\"foo\\\", count: 3)");
        then(greeting).isEqualTo("hi, foo 3");
    }
}
