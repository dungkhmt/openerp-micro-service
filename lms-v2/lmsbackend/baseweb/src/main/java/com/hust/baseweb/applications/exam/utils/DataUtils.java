package com.hust.baseweb.applications.exam.utils;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class DataUtils {

    public static String formatStringValueSql(String value){
        if(value == null){
            return "";
        }
        return value;
    }
}
