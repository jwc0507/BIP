package com.example.week8.utils.customvalidation;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {ConvPointValidator.class})
public @interface ConvPointCheck {
    String message() default "";
    Class<?> [] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
