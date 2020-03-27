package com.github.t1.graphql.client;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class ParametersBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface ParamApi {
        String greeting(String who);
    }

    @Test void shouldCallParamQuery() {
        ParamApi api = fixture.buildClient(ParamApi.class);
        fixture.returnsData("\"greeting\":\"hi, foo\"");

        String greeting = api.greeting("foo");

        then(fixture.query()).isEqualTo("greeting(who: \\\"foo\\\")");
        then(greeting).isEqualTo("hi, foo");
    }


    interface ParamsApi {
        String greeting(String who, int count);
    }

    @Test void shouldCallTwoParamsQuery() {
        ParamsApi api = fixture.buildClient(ParamsApi.class);
        fixture.returnsData("\"greeting\":\"hi, foo 3\"");

        String greeting = api.greeting("foo", 3);

        then(fixture.query()).isEqualTo("greeting(who: \\\"foo\\\", count: 3)");
        then(greeting).isEqualTo("hi, foo 3");
    }
}
