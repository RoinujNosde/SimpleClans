package net.sacredlabyrinth.phaed.simpleclans.hooks.papi;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Placeholders.class)
public @interface Placeholder {

    String value();
    String resolver() default "method_return";
    String config() default "";
}
