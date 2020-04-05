package com.github.t1.graphql.client;

import com.github.t1.graphql.client.api.GraphQlClientException;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN_TYPE;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

class ScalarApiBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();


    interface BoolApi {
        boolean bool();
    }

    @Test void shouldCallBoolQuery() {
        fixture.returnsData("\"bool\":true");
        BoolApi api = fixture.builder().build(BoolApi.class);

        boolean bool = api.bool();

        then(fixture.query()).isEqualTo("bool");
        then(bool).isTrue();
    }


    interface BooleanApi {
        Boolean bool();
    }

    @Test void shouldCallBooleanQuery() {
        fixture.returnsData("\"bool\":true");
        BooleanApi api = fixture.builder().build(BooleanApi.class);

        Boolean bool = api.bool();

        then(fixture.query()).isEqualTo("bool");
        then(bool).isTrue();
    }


    interface ByteApi {
        Byte code();
    }

    @Test void shouldCallByteQuery() {
        fixture.returnsData("\"code\":5");
        ByteApi api = fixture.builder().build(ByteApi.class);

        Byte code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo((byte) 5);
    }


    interface PrimitiveByteApi {
        byte code();
    }

    @Test void shouldCallPrimitiveByteQuery() {
        fixture.returnsData("\"code\":5");
        PrimitiveByteApi api = fixture.builder().build(PrimitiveByteApi.class);

        byte code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo((byte) 5);
    }


    interface ShortApi {
        Short code();
    }

    @Test void shouldCallShortQuery() {
        fixture.returnsData("\"code\":5");
        ShortApi api = fixture.builder().build(ShortApi.class);

        Short code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo((short) 5);
    }


    interface PrimitiveShortApi {
        short code();
    }

    @Test void shouldCallPrimitiveShortQuery() {
        fixture.returnsData("\"code\":5");
        PrimitiveShortApi api = fixture.builder().build(PrimitiveShortApi.class);

        short code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo((short) 5);
    }


    interface IntegerApi {
        Integer code();
    }

    @Test void shouldCallIntegerQuery() {
        fixture.returnsData("\"code\":5");
        IntegerApi api = fixture.builder().build(IntegerApi.class);

        Integer code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo(5);
    }


    interface IntApi {
        int code();
    }

    @Test void shouldCallIntQuery() {
        fixture.returnsData("\"code\":5");
        IntApi api = fixture.builder().build(IntApi.class);

        int code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo(5);
    }


    interface LongApi {
        Long code();
    }

    @Test void shouldCallLongQuery() {
        fixture.returnsData("\"code\":5");
        LongApi api = fixture.builder().build(LongApi.class);

        Long code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo(5L);
    }


    interface PrimitiveLongApi {
        long code();
    }

    @Test void shouldCallPrimitiveLongQuery() {
        fixture.returnsData("\"code\":5");
        PrimitiveLongApi api = fixture.builder().build(PrimitiveLongApi.class);

        long code = api.code();

        then(fixture.query()).isEqualTo("code");
        then(code).isEqualTo(5L);
    }


    interface FloatApi {
        Float number();
    }

    @Test void shouldCallFloatQuery() {
        fixture.returnsData("\"number\":123.456");
        FloatApi api = fixture.builder().build(FloatApi.class);

        Float number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(123.456f);
    }


    interface PrimitiveFloatApi {
        float number();
    }

    @Test void shouldCallPrimitiveFloatQuery() {
        fixture.returnsData("\"number\":123.456");
        PrimitiveFloatApi api = fixture.builder().build(PrimitiveFloatApi.class);

        float number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(123.456f);
    }


    interface BigDecimalApi {
        BigDecimal number();
    }

    @Test void shouldCallReallyLongDecimalQuery() {
        String reallyLongDecimal = "123.45678901234567890123456789012345678901234567890123456789012345678901234567890";
        fixture.returnsData("\"number\":" + reallyLongDecimal);
        BigDecimalApi api = fixture.builder().build(BigDecimalApi.class);

        BigDecimal number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(reallyLongDecimal);
    }

    @Test void shouldCallNotSoLongDecimalQuery() {
        String notSoLongDecimal = "123.456";
        fixture.returnsData("\"number\":\"" + notSoLongDecimal + "\"");
        BigDecimalApi api = fixture.builder().build(BigDecimalApi.class);

        BigDecimal number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(notSoLongDecimal);
    }


    interface PrimitiveDoubleApi {
        double number();
    }

    @Test void shouldCallPrimitiveDoubleQuery() {
        fixture.returnsData("\"number\":123.456");
        PrimitiveDoubleApi api = fixture.builder().build(PrimitiveDoubleApi.class);

        double number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(123.456D);
    }


    interface DoubleApi {
        Double number();
    }

    @Test void shouldCallDoubleQuery() {
        fixture.returnsData("\"number\":123.456");
        DoubleApi api = fixture.builder().build(DoubleApi.class);

        Double number = api.number();

        then(fixture.query()).isEqualTo("number");
        then(number).isEqualTo(123.456D);
    }


    interface CharacterApi {
        Character c();
    }

    @Test void shouldCallCharacterQuery() {
        fixture.returnsData("\"c\":\"a\"");
        CharacterApi api = fixture.builder().build(CharacterApi.class);

        Character c = api.c();

        then(fixture.query()).isEqualTo("c");
        then(c).isEqualTo('a');
    }

    @Test void shouldFailCharacterQueryWithMoreThanOneCharacter() {
        fixture.returnsData("\"c\":\"ab\"");
        CharacterApi api = fixture.builder().build(CharacterApi.class);

        GraphQlClientException thrown = catchThrowableOfType(api::c, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("c");
        then(thrown).hasMessage("invalid value for java.lang.Character field c: 'ab'");
    }


    interface PrimitiveCharApi {
        char c();
    }

    @Test void shouldCallPrimitiveCharQuery() {
        fixture.returnsData("\"c\":\"a\"");
        PrimitiveCharApi api = fixture.builder().build(PrimitiveCharApi.class);

        char c = api.c();

        then(fixture.query()).isEqualTo("c");
        then(c).isEqualTo('a');
    }

    @Test void shouldFailPrimitiveCharQueryWithMoreThanOneCharacter() {
        fixture.returnsData("\"c\":\"ab\"");
        PrimitiveCharApi api = fixture.builder().build(PrimitiveCharApi.class);

        GraphQlClientException thrown = catchThrowableOfType(api::c, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("c");
        then(thrown).hasMessage("invalid value for char field c: 'ab'");
    }


    interface StringApi {
        String greeting();
    }

    @Test void shouldCallStringQuery() {
        fixture.returnsData("\"greeting\":\"dummy-greeting\"");
        StringApi api = fixture.builder().build(StringApi.class);

        String greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting");
        then(greeting).isEqualTo("dummy-greeting");
    }

    @Test void shouldFailStringQueryNotFound() {
        fixture.returns(Response.serverError().type(TEXT_PLAIN_TYPE).entity("failed").build());
        StringApi api = fixture.builder().build(StringApi.class);

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("expected successful status code but got 500 Internal Server Error:\n" +
            "failed");
    }

    @Test void shouldFailOnQueryError() {
        fixture.returns(Response.ok("{\"errors\":[{\"message\":\"failed\"}]}").build());
        StringApi api = fixture.builder().build(StringApi.class);

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("errors from service: [{\"message\":\"failed\"}]:\n" +
            "  {\"query\":\"{ greeting }\"}");
    }

    @Test void shouldFailOnMissingQueryResponse() {
        fixture.returnsData("");
        StringApi api = fixture.builder().build(StringApi.class);

        GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

        then(fixture.query()).isEqualTo("greeting");
        then(thrown).hasMessage("no data for 'greeting':\n  {}");
    }


    interface ScalarWithValueOfApi {
        Integer foo();
    }

    @Test void shouldCallScalarWithValueOfQuery() {
        fixture.returnsData("\"foo\":123456");
        ScalarWithValueOfApi api = fixture.builder().build(ScalarWithValueOfApi.class);

        Integer value = api.foo();

        then(fixture.query()).isEqualTo("foo");
        then(value).isEqualTo(123456);
    }


    interface ScalarWithParseApi {
        LocalDate now();
    }

    @Test void shouldCallScalarWithParseQuery() {
        LocalDate now = LocalDate.now();
        fixture.returnsData("\"now\":\"" + now + "\"");
        ScalarWithParseApi api = fixture.builder().build(ScalarWithParseApi.class);

        LocalDate value = api.now();

        then(fixture.query()).isEqualTo("now");
        then(value).isEqualTo(now);
    }


    interface ScalarWithStringConstructorApi {
        BigInteger foo();
    }

    @Test void shouldCallNotSoBigIntegerScalarWithStringConstructorApiQuery() {
        String bigNumber = "1234";
        fixture.returnsData("\"foo\":" + bigNumber);
        ScalarWithStringConstructorApi api = fixture.builder().build(ScalarWithStringConstructorApi.class);

        BigInteger value = api.foo();

        then(fixture.query()).isEqualTo("foo");
        then(value).isEqualTo(bigNumber);
    }

    @Test void shouldCallVeryBigIntegerScalarWithStringConstructorApiQuery() {
        String bigNumber = "1234567890123456789012345678901234567890123456789012345678901234567890";
        fixture.returnsData("\"foo\":" + bigNumber);
        ScalarWithStringConstructorApi api = fixture.builder().build(ScalarWithStringConstructorApi.class);

        BigInteger value = api.foo();

        then(fixture.query()).isEqualTo("foo");
        then(value).isEqualTo(bigNumber);
    }

    // TODO char/Character from number
    // TODO many invalid value tests
}
