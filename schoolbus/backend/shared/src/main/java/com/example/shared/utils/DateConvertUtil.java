package com.example.shared.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateConvertUtil {

    public static Instant convertStringToInstant(String date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(date, formatter);
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    public static String instantToString(Instant instant) {
        if (instant == null) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.format(formatter);
    }

    public static Instant convertStringTimeStampToInstant(String date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        return localDateTime.atZone(ZoneId.of("GMT+7")).toInstant();
    }

    public static String convertInstantToString(Instant instant) {
        if (instant == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("GMT+7"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return zonedDateTime.format(formatter);
    }

    public static String convertInstantToStringTimeStamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.of("GMT+7"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return zonedDateTime.format(formatter);
    }

    public static Instant addDays(Instant instant, int days) {
        if (instant == null) {
            return null;
        }
        return instant.plusSeconds((long) days * 24 * 60 * 60);
    }
}
