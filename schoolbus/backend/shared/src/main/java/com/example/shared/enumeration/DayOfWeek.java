package com.example.shared.enumeration;

public enum DayOfWeek {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private final String day;

    DayOfWeek(String day) {
        this.day = day;
    }

    public String getValue() {
        return day;
    }
}
