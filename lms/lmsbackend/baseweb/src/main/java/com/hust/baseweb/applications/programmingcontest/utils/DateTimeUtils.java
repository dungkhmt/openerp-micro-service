package com.hust.baseweb.applications.programmingcontest.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateTimeUtils {

    public static final String START_DATE_TIME = "2000-01-01 00:00:00";

    public static final DateFormat ISO_8601_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");

    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(dateTimeFormatter);
    }

    public static List<String> getListDateHavingDay(int d, Date fromDate, Date toDate, String effectiveStartDate) {
        ArrayList<String> L = new ArrayList<String>();
        Date fd = fromDate;
        Date td = toDate;
        Date esd = convertDateTimeStr2Date(effectiveStartDate);
        if (fd.compareTo(esd) < 0) {
            fd = esd;
        }
        if (fd.compareTo(td) > 0) {
            return L;
        }
        System.out.println("fd =  " + fd.toString() + ", YYYY-MM-DD = " + date2YYYYMMDD(fd));
        while (!fd.equals(td)) {
            if (fd.getDay() == d - 1) {
                //L.add(fd.getYear() + "-" + fd.getMonth() + "-" + fd.getDate());
                L.add(date2YYYYMMDD(fd));
            }
            fd = next(fd, 1);
        }
        return L;
    }

    public static List<String> getListDateHavingDay(int d, String fromDate, String toDate, String effectiveStartDate) {
        ArrayList<String> L = new ArrayList<String>();
        Date fd = convertDateTimeStr2Date(fromDate);
        Date td = convertDateTimeStr2Date(toDate);
        Date esd = convertDateTimeStr2Date(effectiveStartDate);
        if (fd.compareTo(esd) < 0) {
            fd = esd;
        }
        if (fd.compareTo(td) > 0) {
            return L;
        }
        System.out.println("fd =  " + fd.toString() + ", YYYY-MM-DD = " + date2YYYYMMDD(fd));
        while (!fd.equals(td)) {
            if (fd.getDay() == d - 1) {
                //L.add(fd.getYear() + "-" + fd.getMonth() + "-" + fd.getDate());
                L.add(date2YYYYMMDD(fd));
            }
            fd = next(fd, 1);
        }
        return L;
    }

    public static String extendDateTime(String dt, int s) {// s seconds
        long d = dateTime2Int(dt);
        return unixTimeStamp2DateTime(d + s);
    }

    public static String next(String dt, int numberDays) {
        Date d = convertDateTimeStr2Date(dt);
        Date nd = next(d, numberDays);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String new_dt = dateFormat.format(nd);
        return new_dt;
    }

    public static Date next(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    public static Date next(Date d, int nbDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.DATE, nbDays);
        return cal.getTime();
    }

    public static int distanceDate(String s_date1, String s_date2) {
        Date d1 = DateTimeUtils.convertYYYYMMDD2Date(s_date1);
        Date d2 = DateTimeUtils.convertYYYYMMDD2Date(s_date2);
        return distance(d1, d2);
    }

    public static int distance(Date d1, Date d2) {
        int d = 0;
        if (d1.compareTo(d2) > 0) {
            Date tmpd = d2;
            while (!tmpd.equals(d1)) {
                tmpd = DateTimeUtils.next(tmpd, 1);
                d++;
            }
            d = -d;
        } else {
            Date tmpd = d1;
            while (!tmpd.equals(d2)) {
                tmpd = DateTimeUtils.next(tmpd, 1);
                d++;
            }
        }
        return d;
    }

    public static String dateMonthYear(Date d) {
        return d.getDate() + "-" + (d.getMonth() + 1) + "-" + d.getYear();
    }

    public static String date2YYYYMMDD(Date d) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        String strDate = df.format(d);
        String s = d.getDate() + "";
        if (s.length() == 1) {
            s = "0" + s;
        }
        strDate = strDate.substring(0, 8) + s;
        return strDate;
    }

    public static Date convertYYYYMMDD2Date(String dt) {
        try {
            //System.out.println(name() + "::dateTime2Int, dt = " + dt);
            //String[] s = dt.split(":");
            //if(s.length < 3) dt += ":00";// add :ss to hh:mm --> hh:mm::ss
            dt += " 00:00:00";
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(dt);
            return date;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Date convertDateTimeStr2Date(String dt) {
        try {
            //System.out.println(name() + "::dateTime2Int, dt = " + dt);
            //String[] s = dt.split(":");
            //if(s.length < 3) dt += ":00";// add :ss to hh:mm --> hh:mm::ss
            //dt += " 00:00:00";
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(dt);
            return date;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // return the distance dt1 - dt2 (in seconds)
    public static long distanceDateTime(String dt1, String dt2) {
        return dateTime2Int(dt1) - dateTime2Int(dt2);
    }

    public static String meanDatetime(String dt1, String dt2) {
        long u1 = dateTime2Int(dt1);
        long u2 = dateTime2Int(dt2);
        long u = (u1 + u2) / 2;
        return unixTimeStamp2DateTime(u);
    }

    // in seconds
    public static long dateTime2Int(String dt) {
        // convert datetime to int (seconds), datetime example is 2016-10-04 10:30:15
		/*
		String[] s = dt.split(" ");
		String[] d = s[0].split("-");
		int year = Integer.valueOf(d[0]);
		int month = Integer.valueOf(d[1]);
		int day = Integer.valueOf(d[2]);
		String[] t = s[1].split(":");
		int hour = Integer.valueOf(t[0]);
		int minute = Integer.valueOf(t[1]);
		int second = Integer.valueOf(t[2]);
		*/
        try {
            //System.out.println(name() + "::dateTime2Int, dt = " + dt);
            String[] s = dt.split(":");
            if (s.length < 3) {
                dt += ":00";// add :ss to hh:mm --> hh:mm::ss
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormat.parse(dt);
            return date.getTime() / 1000;// in seconds
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static String unixTimeStamp2DateTime(long dt) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //dt is measured in seconds and must be converted into milliseconds
        return dateFormat.format(dt * 1000);
    }

    public static String second2HMS(int hms) {
        //String s = "";
        int h = hms / 3600;
        int m = (hms - h * 3600) / 60;
        int s = hms - h * 3600 - m * 60;
        return h + ":" + m + ":" + s;
    }

    public static long computeStartTimePoint(long early1, long late1, long travelTime, long early2, long late2) {
        //[early1,late1] is the time windows of start point
        //[early2,late2] is the time windows of end point
        // travel time from start point to end point
        if (early1 + travelTime >= early2) {
            return early1;
        }
        return early2 - travelTime;
    }

    public static int getHour(String dt) {
        // date time dt is of format yyyy-mm-dd hh:mm:ss
        // return hour
        String[] s = dt.split(" ");
        String[] s1 = s[1].split(":");
        return Integer.valueOf(s1[0].trim());
    }

    public static int getMinute(String dt) {
        // date time dt is of format yyyy-mm-dd hh:mm:ss
        // return hour
        String[] s = dt.split(" ");
        String[] s1 = s[1].split(":");
        return Integer.valueOf(s1[1].trim());
    }

    public static String name() {
        return "DateTimeUtils";
    }

    public static boolean isHighTraffic(String dt) {
        int hour = getHour(dt);
        int minute = getMinute(dt);
        int hm = hour * 60 + minute;
        //System.out.println(name() + "::isHighTraffic, hm = " + hm);
        return (8 * 60 <= hm && hm <= 9 * 60 || 16 * 60 <= hm && hm <= 18 * 60);
    }

    public static String currentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        Date date = new Date();

        //System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
        String DT = dateFormat.format(date);
        System.out.println(DT);
        String[] s = DT.split(":");
        return s[0] + s[1] + s[2];
    }

    public static String getRelativeDateTime(String dt) {
        long start = dateTime2Int(START_DATE_TIME);
        long d = dateTime2Int(dt);
        long new_d = d - start;
        String new_date_time = unixTimeStamp2DateTime(new_d);
        return new_date_time;
    }

    public static String recoverDateTimeFromRelative(String dt) {
        long start = dateTime2Int(START_DATE_TIME);
        long d = dateTime2Int(dt);
        long new_d = d + start;
        String new_date_time = unixTimeStamp2DateTime(new_d);
        return new_date_time;
    }

    public static String std2Digit(int x) {
        String s = x + "";
        while (s.length() < 2) {
            s = "0" + s;
        }
        return s;
    }


    public enum DateTimeFormat{
        DATE_TIME_ISO_FORMAT("yyyy-MM-dd HH:mm:ss");

        private final String value;

        DateTimeFormat(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static ZonedDateTime dateToZoneDateTime(Date date){
        return date.toInstant().atZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    }

    public static String zonedDateTimeToString(ZonedDateTime zonedDateTime, DateTimeFormat dateTimeFormat){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat.getValue());
        return zonedDateTime.format(formatter);
    }

    public static String dateToString(Date date, DateTimeFormat format){
        ZonedDateTime zonedDateTime = dateToZoneDateTime(date);
        return zonedDateTimeToString(zonedDateTime, format);
    }

    public static  Date minusMinutesDate(Date date, Long minutes){
        ZonedDateTime zonedDateTime = dateToZoneDateTime(date);
        zonedDateTime = zonedDateTime.minusMinutes(minutes);
        return Date.from(zonedDateTime.toInstant());
    }

    public static Date addMinutesDate(Date date, Long minutes){
        ZonedDateTime zonedDateTime = dateToZoneDateTime(date);
        zonedDateTime = zonedDateTime.plusMinutes(minutes);
        return Date.from(zonedDateTime.toInstant());
    }

    public static void main(String[] args){
        System.out.println("test");
    }
}
