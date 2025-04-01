package com.hust.baseweb.applications.exam.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class DataUtils {

    public static Long safeToLong(Object obj1, Long defaultValue) {
        Long result = defaultValue;
        if (obj1 != null) {
            if (obj1 instanceof BigDecimal) {
                return ((BigDecimal) obj1).longValue();
            }
            if (obj1 instanceof BigInteger) {
                return ((BigInteger) obj1).longValue();
            }
            try {
                result = Long.parseLong(obj1.toString());
            } catch (Exception ignored) {
                log.error(ignored.getMessage(), ignored);
            }
        }

        return result;
    }

    /**
     * @param obj1 Object
     * @return Long
     */
    public static Long safeToLong(Object obj1) {
        return safeToLong(obj1, null);
    }

    public static Double safeToDouble(Object obj1, Double defaultValue) {
        Double result = defaultValue;
        if (obj1 != null) {
            try {
                result = Double.parseDouble(obj1.toString());
            } catch (Exception ignored) {
                log.error(ignored.getMessage(), ignored);
            }
        }

        return result;
    }

    public static Double safeToDouble(Object obj1) {
        return safeToDouble(obj1, null);
    }

    public static Short safeToShort(Object obj1, Short defaultValue) {
        Short result = defaultValue;
        if (obj1 != null) {
            try {
                result = Short.parseShort(obj1.toString());
            } catch (Exception ignored) {
                log.error(ignored.getMessage(), ignored);
            }
        }

        return result;
    }

    /**
     * @param obj1
     * @param defaultValue
     * @return
     * @author phuvk
     */
    public static Integer safeToInt(Object obj1, Integer defaultValue) {
        Integer result = defaultValue;
        if (obj1 != null) {
            try {
                result = Integer.parseInt(obj1.toString());
            } catch (Exception ignored) {
                log.error(ignored.getMessage(), ignored);
            }
        }

        return result;
    }

    /**
     * @param obj1 Object
     * @return int
     */
    public static Integer safeToInt(Object obj1) {
        return safeToInt(obj1, null);
    }

    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1, String defaultValue) {
        if (obj1 == null || obj1.toString().isEmpty()) {
            return defaultValue;
        }

        return obj1.toString();
    }

    public static BigDecimal safeToBigDecimal(Object value) {
        return new BigDecimal(value.toString());
    }
    public static Boolean safeToBoolean(Object obj1) {
        if (obj1 == null || obj1 instanceof Boolean) {
            return (Boolean) obj1;
        }
        return false;
    }

    /**
     * @param obj1 Object
     * @return String
     */
    public static String safeToString(Object obj1) {
        return safeToString(obj1, null);
    }

    public static Instant safeToInstant(Object obj) {
        if (obj != null) {
            if (obj instanceof Instant) {
                return (Instant) obj;
            } else if (obj instanceof Timestamp) {
                return ((Timestamp) obj).toInstant();
            }
        }
        return null;
    }

    public static Date safeToDate(Object obj) {
        if (obj != null) {
            if (obj instanceof Date) {
                return (Date) obj;
            }
        }
        return null;
    }

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

    public static LocalDateTime formatStringToLocalDateTime(String value){
        if(value == null){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(value, formatter);
    }

    public static LocalDateTime formatStringToLocalDateTimeFull(String value){
        if(value == null){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        ZonedDateTime zonedDateTime = LocalDateTime.parse(value, formatter).atZone(ZoneOffset.UTC);
        return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime getTimeNowWithZone(){
        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneOffset.UTC);
        return zonedDateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static boolean stringIsNotNullOrEmpty(String value){
        if(StringUtils.isNotEmpty(value) && StringUtils.isNotBlank(value)){
            return true;
        }
        return false;
    }

    public static String escapeSpecialCharacters(String value) {
        return value.replace("/", "\\/")
                    .replace("%", "\\%")
                    .replace("_", "\\_")
                    .replace("&", "\\&")
                    .replace(".", "\\.");
    }
}
