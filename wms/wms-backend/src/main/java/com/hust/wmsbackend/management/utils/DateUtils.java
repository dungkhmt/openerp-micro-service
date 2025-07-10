package com.hust.wmsbackend.management.utils;

import org.joda.time.DateTimeComparator;

import java.util.Date;

public class DateUtils {

    public static boolean isEqual(Date a, Date b) {
        // return true if a before b
        if (a == null || b == null) {
            throw new RuntimeException("Date to compare is null");
        }
        return DateTimeComparator.getDateOnlyInstance().compare(a, b) == 0;
    }

    public static boolean isBeforeOrEqual(Date a, Date b) {
        // return true if a before b
        if (a == null || b == null) {
            throw new RuntimeException("Date to compare is null");
        }
        return DateTimeComparator.getDateOnlyInstance().compare(a, b) <= 0;
    }

    public static boolean isNowBetween(Date now, Date start, Date end) {
        if (now == null || start == null) {
            return false;
        }
        return DateTimeComparator.getDateOnlyInstance().compare(start, now) <= 0 &&
                (end == null || DateTimeComparator.getDateOnlyInstance().compare(now, end) >= 0);
    }

    public static boolean isOverlap(Date s1, Date e1, Date s2, Date e2) {
        DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();

        if (s1 == null || s2 == null) {
            throw new RuntimeException("Start date comparator for overlap checking is must not be null");
        }

        if (e1 == null && e2 == null) {
            return true;
        }

        if (e1 == null) {
            return comparator.compare(s1, e2) <= 0;
        }

        if (e2 == null) {
            return comparator.compare(e1, s2) >= 0;
        }

        return (comparator.compare(s1, s2) <= 0 && comparator.compare(e1, s2) >= 0)
                || (comparator.compare(s1, e2) <= 0 && comparator.compare(e1, e2) >= 0)
                || (comparator.compare(s1, s2) <= 0 && comparator.compare(e1, e2) >= 0)
                || (comparator.compare(s1, s2) >= 0 && comparator.compare(e1, e2) <= 0);
        // refer: https://www.codespeedy.com/check-if-two-date-ranges-overlap-or-not-in-java/#:~:text=compareTo(s2)%3C0%20can,as%20e1%20comes%20after%20s2.&text=This%20is%20how%20we%20can,Overlap%20or%20not%20in%20Java.
    }
}
