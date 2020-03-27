package com.github.t1.graphql.client.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME) @Target(TYPE)
public @interface GraphQlClientApi {
    String endpoint() default "";

    String configKey() default "";
}
