package com.hust.openerp.taskmanagement.hr_management.reflection.classBinding;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ReflectionClassKeys.class)
@Target(ElementType.TYPE)
public @interface ReflectionClassKey {
    Class<?> value();
}
