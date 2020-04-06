package test.unit;

import com.github.t1.graphql.client.api.GraphQlClientException;
import org.junit.jupiter.api.Nested;
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

    interface BooleanApi {
        Boolean bool();
    }

    @Nested class BooleanBehavior {
        @Test void shouldCallBoolQuery() {
            fixture.returnsData("\"bool\":true");
            BoolApi api = fixture.builder().build(BoolApi.class);

            boolean bool = api.bool();

            then(fixture.query()).isEqualTo("bool");
            then(bool).isTrue();
        }


        @Test void shouldCallBooleanQuery() {
            fixture.returnsData("\"bool\":true");
            BooleanApi api = fixture.builder().build(BooleanApi.class);

            Boolean bool = api.bool();

            then(fixture.query()).isEqualTo("bool");
            then(bool).isTrue();
        }
    }


    interface ByteApi {
        Byte code();
    }

    interface PrimitiveByteApi {
        byte code();
    }

    @Nested class ByteBehavior {
        @Test void shouldCallByteQuery() {
            fixture.returnsData("\"code\":5");
            ByteApi api = fixture.builder().build(ByteApi.class);

            Byte code = api.code();

            then(fixture.query()).isEqualTo("code");
            then(code).isEqualTo((byte) 5);
        }


        @Test void shouldCallPrimitiveByteQuery() {
            fixture.returnsData("\"code\":5");
            PrimitiveByteApi api = fixture.builder().build(PrimitiveByteApi.class);

            byte code = api.code();

            then(fixture.query()).isEqualTo("code");
            then(code).isEqualTo((byte) 5);
        }

        @Test void shouldFailByteFromTooBigNumberQuery() {
            int tooBig = (int) Byte.MAX_VALUE + 1;
            fixture.returnsData("\"code\":" + tooBig);
            ByteApi api = fixture.builder().build(ByteApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Byte value for " + ByteApi.class.getName() + "#code: " + tooBig);
        }

        @Test void shouldFailByteFromNegativeNumberQuery() {
            int tooSmall = (int) Byte.MIN_VALUE - 1;
            fixture.returnsData("\"code\":" + tooSmall);
            ByteApi api = fixture.builder().build(ByteApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Byte value for " + ByteApi.class.getName() + "#code: " + tooSmall);
        }
    }


    interface CharacterApi {
        Character code();
    }

    interface PrimitiveCharApi {
        char code();
    }

    @Nested class CharacterBehavior {
        @Test void shouldCallCharacterFromStringQuery() {
            fixture.returnsData("\"code\":\"a\"");
            CharacterApi api = fixture.builder().build(CharacterApi.class);

            Character c = api.code();

            then(fixture.query()).isEqualTo("code");
            then(c).isEqualTo('a');
        }

        @Test void shouldFailCharacterFromStringQueryWithMoreThanOneCharacter() {
            fixture.returnsData("\"code\":\"ab\"");
            CharacterApi api = fixture.builder().build(CharacterApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Character value for " + CharacterApi.class.getName() + "#code: \"ab\"");
        }

        @Test void shouldCallCharacterFromNumberQuery() {
            fixture.returnsData("\"code\":97");
            CharacterApi api = fixture.builder().build(CharacterApi.class);

            Character c = api.code();

            then(fixture.query()).isEqualTo("code");
            then(c).isEqualTo('a');
        }

        @Test void shouldFailCharacterFromTooBigNumberQuery() {
            int tooBig = (int) Character.MAX_VALUE + 1;
            fixture.returnsData("\"code\":" + tooBig);
            CharacterApi api = fixture.builder().build(CharacterApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Character value for " + CharacterApi.class.getName() + "#code: " + tooBig);
        }

        @Test void shouldFailCharacterFromNegativeNumberQuery() {
            fixture.returnsData("\"code\":-15");
            CharacterApi api = fixture.builder().build(CharacterApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Character value for " + CharacterApi.class.getName() + "#code: -15");
        }


        @Test void shouldCallPrimitiveCharQuery() {
            fixture.returnsData("\"code\":\"a\"");
            PrimitiveCharApi api = fixture.builder().build(PrimitiveCharApi.class);

            char c = api.code();

            then(fixture.query()).isEqualTo("code");
            then(c).isEqualTo('a');
        }

        @Test void shouldFailPrimitiveCharQueryWithMoreThanOneCharacter() {
            fixture.returnsData("\"code\":\"ab\"");
            PrimitiveCharApi api = fixture.builder().build(PrimitiveCharApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid char value for " + PrimitiveCharApi.class.getName() + "#code: \"ab\"");
        }
    }


    interface ShortApi {
        Short code();
    }

    interface PrimitiveShortApi {
        short code();
    }

    @Nested class ShortBehavior {
        @Test void shouldCallShortQuery() {
            fixture.returnsData("\"code\":5");
            ShortApi api = fixture.builder().build(ShortApi.class);

            Short code = api.code();

            then(fixture.query()).isEqualTo("code");
            then(code).isEqualTo((short) 5);
        }

        @Test void shouldFailToCallTooSmallShortQuery() {
            int tooSmall = (int) Short.MIN_VALUE - 1;
            fixture.returnsData("\"code\":" + tooSmall);
            ShortApi api = fixture.builder().build(ShortApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Short value for test.unit.ScalarApiBehavior$ShortApi#code: " + tooSmall);
        }

        @Test void shouldFailToCallTooBigShortQuery() {
            int tooBig = (int) Short.MAX_VALUE + 1;
            fixture.returnsData("\"code\":" + tooBig);
            ShortApi api = fixture.builder().build(ShortApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Short value for test.unit.ScalarApiBehavior$ShortApi#code: " + tooBig);
        }


        @Test void shouldCallPrimitiveShortQuery() {
            fixture.returnsData("\"code\":5");
            PrimitiveShortApi api = fixture.builder().build(PrimitiveShortApi.class);

            short code = api.code();

            then(fixture.query()).isEqualTo("code");
            then(code).isEqualTo((short) 5);
        }
    }


    interface IntegerApi {
        Integer code();
    }

    interface IntApi {
        int code();
    }

    @Nested class IntegerBehavior {
        @Test void shouldCallIntegerQuery() {
            fixture.returnsData("\"code\":5");
            IntegerApi api = fixture.builder().build(IntegerApi.class);

            Integer code = api.code();

            then(fixture.query()).isEqualTo("code");
            then(code).isEqualTo(5);
        }

        @Test void shouldFailToCallTooSmallIntegerQuery() {
            long tooSmall = (long) Integer.MIN_VALUE - 1;
            fixture.returnsData("\"code\":" + tooSmall);
            IntegerApi api = fixture.builder().build(IntegerApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Integer value for test.unit.ScalarApiBehavior$IntegerApi#code: " + tooSmall);
        }

        @Test void shouldFailToCallTooBigIntegerQuery() {
            long tooBig = (long) Integer.MAX_VALUE + 1;
            fixture.returnsData("\"code\":" + tooBig);
            IntegerApi api = fixture.builder().build(IntegerApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Integer value for test.unit.ScalarApiBehavior$IntegerApi#code: " + tooBig);
        }


        @Test void shouldCallIntQuery() {
            fixture.returnsData("\"code\":5");
            IntApi api = fixture.builder().build(IntApi.class);

            int code = api.code();

            then(fixture.query()).isEqualTo("code");
            then(code).isEqualTo(5);
        }
    }


    interface LongApi {
        Long code();
    }

    interface PrimitiveLongApi {
        long code();
    }

    @Nested class LongBehavior {
        @Test void shouldCallLongQuery() {
            fixture.returnsData("\"code\":5");
            LongApi api = fixture.builder().build(LongApi.class);

            Long code = api.code();

            then(fixture.query()).isEqualTo("code");
            then(code).isEqualTo(5L);
        }

        @Test void shouldFailToCallTooSmallLongQuery() {
            String tooSmall = "-9223372036854775809";
            fixture.returnsData("\"code\":" + tooSmall);
            LongApi api = fixture.builder().build(LongApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Long value for " + LongApi.class.getName() + "#code: " + tooSmall);
        }

        @Test void shouldFailToCallTooBigLongQuery() {
            String tooBig = "9223372036854775808";
            fixture.returnsData("\"code\":" + tooBig);
            LongApi api = fixture.builder().build(LongApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::code, GraphQlClientException.class);

            then(thrown).hasMessage("invalid java.lang.Long value for test.unit.ScalarApiBehavior$LongApi#code: " + tooBig);
        }


        @Test void shouldCallPrimitiveLongQuery() {
            fixture.returnsData("\"code\":5");
            PrimitiveLongApi api = fixture.builder().build(PrimitiveLongApi.class);

            long code = api.code();

            then(fixture.query()).isEqualTo("code");
            then(code).isEqualTo(5L);
        }
    }


    interface FloatApi {
        Float number();
    }

    interface PrimitiveFloatApi {
        float number();
    }

    @Nested class FloatBehavior {
        @Test void shouldCallFloatQuery() {
            fixture.returnsData("\"number\":123.456");
            FloatApi api = fixture.builder().build(FloatApi.class);

            Float number = api.number();

            then(fixture.query()).isEqualTo("number");
            then(number).isEqualTo(123.456f);
        }


        @Test void shouldCallPrimitiveFloatQuery() {
            fixture.returnsData("\"number\":123.456");
            PrimitiveFloatApi api = fixture.builder().build(PrimitiveFloatApi.class);

            float number = api.number();

            then(fixture.query()).isEqualTo("number");
            then(number).isEqualTo(123.456f);
        }
    }


    interface DoubleApi {
        Double number();
    }

    interface PrimitiveDoubleApi {
        double number();
    }

    @Nested class DoubleBehavior {
        @Test void shouldCallDoubleQuery() {
            fixture.returnsData("\"number\":123.456");
            DoubleApi api = fixture.builder().build(DoubleApi.class);

            Double number = api.number();

            then(fixture.query()).isEqualTo("number");
            then(number).isEqualTo(123.456D);
        }


        @Test void shouldCallPrimitiveDoubleQuery() {
            fixture.returnsData("\"number\":123.456");
            PrimitiveDoubleApi api = fixture.builder().build(PrimitiveDoubleApi.class);

            double number = api.number();

            then(fixture.query()).isEqualTo("number");
            then(number).isEqualTo(123.456D);
        }
    }


    interface BigDecimalApi {
        BigDecimal number();
    }

    @Nested class BigDecimalBehavior {
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
    }


    interface StringApi {
        String greeting();
    }

    interface ScalarWithValueOfApi {
        Integer foo();
    }

    interface ScalarWithParseApi {
        LocalDate now();
    }

    interface ScalarWithStringConstructorApi {
        BigInteger foo();
    }

    @Nested class StringBehavior {
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

            then(thrown).hasMessage("expected successful status code but got 500 Internal Server Error:\n" +
                "failed");
        }

        @Test void shouldFailOnQueryError() {
            fixture.returns(Response.ok("{\"errors\":[{\"message\":\"failed\"}]}").build());
            StringApi api = fixture.builder().build(StringApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

            then(thrown).hasMessage("errors from service: [{\"message\":\"failed\"}]:\n" +
                "  {\"query\":\"{ greeting }\"}");
        }

        @Test void shouldFailOnMissingQueryResponse() {
            fixture.returnsData("");
            StringApi api = fixture.builder().build(StringApi.class);

            GraphQlClientException thrown = catchThrowableOfType(api::greeting, GraphQlClientException.class);

            then(thrown).hasMessage("no data for 'greeting':\n  {}");
        }


        @Test void shouldCallScalarWithValueOfQuery() {
            fixture.returnsData("\"foo\":123456");
            ScalarWithValueOfApi api = fixture.builder().build(ScalarWithValueOfApi.class);

            Integer value = api.foo();

            then(fixture.query()).isEqualTo("foo");
            then(value).isEqualTo(123456);
        }


        @Test void shouldCallScalarWithParseQuery() {
            LocalDate now = LocalDate.now();
            fixture.returnsData("\"now\":\"" + now + "\"");
            ScalarWithParseApi api = fixture.builder().build(ScalarWithParseApi.class);

            LocalDate value = api.now();

            then(fixture.query()).isEqualTo("now");
            then(value).isEqualTo(now);
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
    }

    // TODO more invalid value tests
}
