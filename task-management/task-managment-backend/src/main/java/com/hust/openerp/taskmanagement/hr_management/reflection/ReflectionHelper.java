package com.hust.openerp.taskmanagement.hr_management.reflection;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionHelper {
    public static <T> Set<Class<? extends T>> getConcreteClasses(String packageName, Class<T> type) {
        Reflections reflections = new Reflections(packageName, Scanners.SubTypes);

        Set<Class<? extends T>> classes = reflections.getSubTypesOf(type);

        return classes.stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .filter(clazz -> !Modifier.isInterface(clazz.getModifiers()))
                .collect(Collectors.toSet());
    }
}
