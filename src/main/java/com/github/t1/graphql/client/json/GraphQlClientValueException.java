package com.github.t1.graphql.client.json;

import com.github.t1.graphql.client.api.GraphQlClientException;

class GraphQlClientValueException extends GraphQlClientException {
    static void check(Location location, Object value, boolean expression) {
        if (!expression)
            throw new GraphQlClientValueException(location, value);
    }

    GraphQlClientValueException(Location location, Object value) {
        super(message(location, value));
    }

    GraphQlClientValueException(Location location, Object value, Throwable cause) {
        super(message(location, value), cause);
    }

    private static String message(Location location, Object value) {
        return "invalid " + location + ": " + value;
    }
}
