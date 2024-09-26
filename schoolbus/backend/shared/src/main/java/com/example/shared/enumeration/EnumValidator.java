package com.example.shared.enumeration;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidatorConstraint.class)
public @interface EnumValidator {

  Class<? extends Enum<?>> enumClass();

  String message() default "invalid value";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}