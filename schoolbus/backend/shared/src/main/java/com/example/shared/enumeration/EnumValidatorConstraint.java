package com.example.shared.enumeration;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidatorConstraint implements ConstraintValidator<EnumValidator, Enum<?>> {

  Set<String> values;

  @Override
  public void initialize(EnumValidator constraintAnnotation) {
    values = Stream.of(constraintAnnotation.enumClass().getEnumConstants())
        .map(Enum::name)
        .collect(Collectors.toSet());
  }

  @Override
  public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
    if (value == null) {
      return true; // Allow null values
    }
    return values.contains(value.name());
  }
}