package com.hust.openerp.taskmanagement.hr_management.reflection.enumBinding;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ReflectionEnumKeys.class)
@Target(ElementType.TYPE)
public @interface ReflectionEnumKey {
    String value() default "";
}
