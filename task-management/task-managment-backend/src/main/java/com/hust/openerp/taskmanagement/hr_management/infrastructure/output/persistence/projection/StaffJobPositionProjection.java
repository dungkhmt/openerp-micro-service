package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.projection;

import java.time.LocalDate;

public interface StaffJobPositionProjection {
    String getUserLoginId();
    String getJobPositionCode();
    String getJobPositionName();
    String getDescription();
    String getStatus();
    LocalDate getFromDate();
    LocalDate getThruDate();
}
