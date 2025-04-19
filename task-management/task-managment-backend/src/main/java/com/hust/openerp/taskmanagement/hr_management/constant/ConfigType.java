package com.hust.openerp.taskmanagement.hr_management.constant;

public enum ConfigType {
    INT,
    TIME;

    public Object parseValue(String value) {
        return switch (this) {
            case INT -> Integer.parseInt(value);
            case TIME -> java.time.LocalTime.parse(value);
        };
    }

    public boolean isValidFormat(String value) {
        try {
            parseValue(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
