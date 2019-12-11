package org.event.driven.light.omegacommon.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface CreateEvent {
    int timeout() default 0;

    String compensationMethod() default "";

    String approveMethod() default "";
}
