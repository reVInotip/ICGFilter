package model.filters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {
    String descr() default "";
    String icon() default "";
}
