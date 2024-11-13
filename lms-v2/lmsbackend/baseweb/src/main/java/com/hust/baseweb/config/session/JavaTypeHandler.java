package com.hust.baseweb.config.session;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

final class JavaTypeHandler {

    static <T> JavaType getJavaType(Class<T> clazz) {
        return TypeFactory.defaultInstance().constructType(clazz);
    }

}
