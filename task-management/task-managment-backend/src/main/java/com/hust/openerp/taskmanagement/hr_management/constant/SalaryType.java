package com.hust.openerp.taskmanagement.hr_management.constant;

public enum SalaryType {
    MONTHLY,
    WEEKLY,
    HOURLY
    ;
    public static SalaryType getDefaultValue() {
        return MONTHLY;
    }
}
