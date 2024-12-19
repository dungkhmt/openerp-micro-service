package com.hust.baseweb.applications.exam.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataUtils {

    public static String formatStringValueSql(String value){
        if(value == null){
            return "";
        }
        return value;
    }

    public static LocalDateTime formatStringValueSqlToLocalDateTime(String value, boolean start){
        if(value == null){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(start){
            return LocalDate.parse(value, formatter).atStartOfDay();
        }else{
            return LocalDate.parse(value, formatter).atTime(23, 59, 59);
        }
    }
}
