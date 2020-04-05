package test.unit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.BDDAssertions.then;

class NestedBehavior {
    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface StringSetApi {
        Set<String> greetings();
    }

    @Test void shouldCallStringSetQuery() {
        fixture.returnsData("\"greetings\":[\"a\",\"b\"]");
        StringSetApi api = fixture.builder().build(StringSetApi.class);

        Set<String> greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
    }


    interface StringListApi {
        List<String> greetings();
    }

    @Test void shouldCallStringListQuery() {
        fixture.returnsData("\"greetings\":[\"a\",\"b\"]");
        StringListApi api = fixture.builder().build(StringListApi.class);

        List<String> greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
    }


    interface StringArrayApi {
        String[] greetings();
    }

    @Test void shouldCallStringArrayQuery() {
        fixture.returnsData("\"greetings\":[\"a\",\"b\"]");
        StringArrayApi api = fixture.builder().build(StringArrayApi.class);

        String[] greetings = api.greetings();

        then(fixture.query()).isEqualTo("greetings");
        then(greetings).containsExactly("a", "b");
    }


    interface ObjectApi {
        Greeting greeting();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class Greeting {
        String text;
        int code;
    }

    @Test void shouldCallObjectQuery() {
        fixture.returnsData("\"greeting\":{\"text\":\"foo\",\"code\":5}");
        ObjectApi api = fixture.builder().build(ObjectApi.class);

        Greeting greeting = api.greeting();

        then(fixture.query()).isEqualTo("greeting {text code}");
        then(greeting).isEqualTo(new Greeting("foo", 5));
    }


    interface ObjectListApi {
        List<Greeting> greetings();
    }

    @Test void shouldCallObjectListQuery() {
        fixture.returnsData("\"greetings\":[{\"text\":\"a\",\"code\":1},{\"text\":\"b\",\"code\":2}]");
        ObjectListApi api = fixture.builder().build(ObjectListApi.class);

        List<Greeting> greeting = api.greetings();

        then(fixture.query()).isEqualTo("greetings {text code}");
        then(greeting).containsExactly(
            new Greeting("a", 1),
            new Greeting("b", 2));
    }


    interface StringContainerApi {
        StringContainer container();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class StringContainer {
        String greeting;
        int count;
    }

    @Test void shouldCallNestedStringQuery() {
        fixture.returnsData("\"container\":{\"greeting\":\"hi\",\"count\":5}");
        StringContainerApi api = fixture.builder().build(StringContainerApi.class);

        StringContainer container = api.container();

        then(fixture.query()).isEqualTo("container {greeting count}");
        then(container).isEqualTo(new StringContainer("hi", 5));
    }


    interface GreetingContainerApi {
        GreetingContainer container();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class GreetingContainer {
        Greeting greeting;
        int count;
    }

    @Test void shouldCallNestedObjectQuery() {
        fixture.returnsData("\"container\":{\"greeting\":{\"text\":\"a\",\"code\":1},\"count\":3}");
        GreetingContainerApi api = fixture.builder().build(GreetingContainerApi.class);

        GreetingContainer container = api.container();

        then(fixture.query()).isEqualTo("container {greeting{text code} count}");
        then(container).isEqualTo(new GreetingContainer(
            new Greeting("a", 1), 3));
    }


    interface GreetingsContainerApi {
        GreetingsContainer container();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class GreetingsContainer {
        public static final String CONST = "don't query static fields";

        List<Greeting> greetings;
        int count;
    }

    @Test void shouldCallNestedListQuery() {
        fixture.returnsData("\"container\":{\"greetings\":[" +
            "{\"text\":\"a\",\"code\":1},{\"text\":\"b\",\"code\":2}" +
            "],\"count\":3}");
        GreetingsContainerApi api = fixture.builder().build(GreetingsContainerApi.class);

        GreetingsContainer container = api.container();

        then(fixture.query()).isEqualTo("container {greetings{text code} count}");
        then(container).isEqualTo(new GreetingsContainer(
            asList(new Greeting("a", 1), new Greeting("b", 2)), 3));
    }


    interface WrappedGreetingApi {
        WrappedGreetingContainer container();
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class WrappedGreetingContainer {
        Wrapper<Greeting> greeting;
        int count;
    }

    @AllArgsConstructor @NoArgsConstructor(force = true)
    @Data public static class Wrapper<T> {
        private T value;
    }

    @Test void shouldCallWrappedGreetingQuery() {
        fixture.returnsData("\"container\":{\"greeting\":{" +
            "\"value\":{\"text\":\"a\",\"code\":1}}," +
            "\"count\":3}");
        WrappedGreetingApi api = fixture.builder().build(WrappedGreetingApi.class);

        WrappedGreetingContainer container = api.container();

        then(fixture.query()).isEqualTo("container {greeting{value{text code}} count}");
        then(container).isEqualTo(new WrappedGreetingContainer(
            new Wrapper<>(new Greeting("a", 1)), 3));
    }


    interface ClassWithTransientAndStaticFieldsApi {
        ClassWithTransientAndStaticFields foo();
    }

    @SuppressWarnings({"unused"})
    @NoArgsConstructor @ToString @EqualsAndHashCode
    public static class ClassWithTransientAndStaticFields {
        public static final String TO_BE_IGNORED = "foo";
        private static final String ALSO_TO_BE_IGNORED = "bar";
        private transient boolean ignoreMe;

        String text;
        int code;

        public ClassWithTransientAndStaticFields(String text, int code) {
            this.text = text;
            this.code = code;
        }
    }

    @Test void shouldCallObjectQueryWithSpecialFields() {
        fixture.returnsData("\"foo\":{\"text\":\"foo\",\"code\":5,\"ignoreMe\":true}");
        ClassWithTransientAndStaticFieldsApi api = fixture.builder().build(ClassWithTransientAndStaticFieldsApi.class);

        ClassWithTransientAndStaticFields foo = api.foo();

        then(fixture.query()).isEqualTo("foo {text code}");
        then(foo).isEqualTo(new ClassWithTransientAndStaticFields("foo", 5));
        then(foo.ignoreMe).isFalse();
    }

    // TODO map, set, inherited fields
}
