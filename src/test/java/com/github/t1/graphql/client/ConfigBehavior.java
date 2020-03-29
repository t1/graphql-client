package com.github.t1.graphql.client;

import com.github.t1.graphql.client.api.GraphQlClientApi;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

class ConfigBehavior {
    private static final String API_URL_CONFIG_KEY = Api.class.getName() + "/mp-graphql/url";

    private final GraphQlClientFixture fixture = new GraphQlClientFixture();

    interface Api {
        boolean foo();
    }

    @Test void shouldFailToLoadMissingEndpointConfig() {
        fixture.endpoint(null);

        Throwable thrown = catchThrowableOfType(() -> fixture.buildClient(Api.class), NoSuchElementException.class);

        then(thrown).hasMessage("Property " + API_URL_CONFIG_KEY + " not found");
    }

    @Test void shouldLoadEndpointConfig() {
        System.setProperty(API_URL_CONFIG_KEY, "http://dummy-endpoint");
        try {
            fixture.endpoint(null);
            fixture.returnsData("\"foo\":true");
            Api api = fixture.buildClient(Api.class);

            boolean foo = api.foo();

            then(fixture.query()).isEqualTo("foo");
            then(foo).isTrue();
        } finally {
            System.clearProperty(API_URL_CONFIG_KEY);
        }
    }

    @Test void shouldLoadEndpointFromKeyConfig() {
        System.setProperty("dummy-config-key/mp-graphql/url", "http://dummy-endpoint");
        try {
            fixture.endpoint(null);
            fixture.configKey("dummy-config-key");
            fixture.returnsData("\"foo\":true");
            Api api = fixture.buildClient(Api.class);

            boolean foo = api.foo();

            then(fixture.query()).isEqualTo("foo");
            then(foo).isTrue();
        } finally {
            System.clearProperty("dummy-config-key");
        }
    }

    @GraphQlClientApi(endpoint = "http://dummy-endpoint")
    interface ConfiguredEndpointApi {
        boolean foo();
    }

    @Test void shouldLoadAnnotatedEndpointConfig() {
        fixture.endpoint(null);
        fixture.returnsData("\"foo\":true");
        ConfiguredEndpointApi api = fixture.buildClient(ConfiguredEndpointApi.class);

        boolean foo = api.foo();

        then(fixture.query()).isEqualTo("foo");
        then(foo).isTrue();
    }

    @GraphQlClientApi(configKey = "dummy-config-key")
    interface ConfiguredKeyApi {
        boolean foo();
    }

    @Test void shouldLoadAnnotatedKeyConfig() {
        System.setProperty("dummy-config-key/mp-graphql/url", "http://dummy-endpoint");
        try {
            fixture.endpoint(null);
            fixture.returnsData("\"foo\":true");
            ConfiguredKeyApi api = fixture.buildClient(ConfiguredKeyApi.class);

            boolean foo = api.foo();

            then(fixture.query()).isEqualTo("foo");
            then(foo).isTrue();
        } finally {
            System.clearProperty("dummy-config-key");
        }
    }
}
