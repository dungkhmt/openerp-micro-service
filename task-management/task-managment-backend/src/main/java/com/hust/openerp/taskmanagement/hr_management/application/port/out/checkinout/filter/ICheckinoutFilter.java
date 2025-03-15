package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkinout.filter;

import com.hust.openerp.taskmanagement.hr_management.constant.CheckinoutType;

import java.time.LocalDate;

public interface ICheckinoutFilter {
    String getUserId();
    LocalDate getDate();
    CheckinoutType getType();
}
