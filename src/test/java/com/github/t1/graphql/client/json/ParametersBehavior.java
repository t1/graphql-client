package com.github.t1.graphql.client.json;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

class ParametersBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface ParamApi {
        String greeting(String who);
    }

    @Test void shouldCallParamQuery() {
        fixture.returnsData("'greeting':'hi, foo'");
        ParamApi api = fixture.builder().build(ParamApi.class);

        String greeting = api.greeting("foo");

        then(fixture.query()).isEqualTo("greeting(who: 'foo')");
        then(greeting).isEqualTo("hi, foo");
    }


    interface ParamsApi {
        String greeting(String who, int count);
    }

    @Test void shouldCallTwoParamsQuery() {
        fixture.returnsData("'greeting':'hi, foo 3'");
        ParamsApi api = fixture.builder().build(ParamsApi.class);

        String greeting = api.greeting("foo", 3);

        then(fixture.query()).isEqualTo("greeting(who: 'foo', count: 3)");
        then(greeting).isEqualTo("hi, foo 3");
    }


    interface BooleanParamApi {
        String greeting(boolean really);
    }

    @Test void shouldCallBooleanParamQuery() {
        fixture.returnsData("'greeting':'ho'");
        BooleanParamApi api = fixture.builder().build(BooleanParamApi.class);

        String greeting = api.greeting(true);

        then(fixture.query()).isEqualTo("greeting(really: true)");
        then(greeting).isEqualTo("ho");
    }


    interface ObjectParamApi {
        Greeting say(Greeting greet);
    }

    @AllArgsConstructor @NoArgsConstructor
    @Data static class Greeting {
        String text;
        int count;
    }

    @Test void shouldCallObjectParamQuery() {
        fixture.returnsData("'say':{'text':'ho','count':3}");
        ObjectParamApi api = fixture.builder().build(ObjectParamApi.class);

        Greeting greeting = api.say(new Greeting("hi", 5));

        then(fixture.query()).isEqualTo("say(greet: {text: 'hi', count: 5}) {text count}");
        then(greeting).isEqualTo(new Greeting("ho", 3));
    }

    // TODO array params + nested
    // TODO params as variables?
}
