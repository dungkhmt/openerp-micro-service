package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceType;
import com.hust.openerp.taskmanagement.util.WorkTimeCalculator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class PayrollModel {
    private UUID id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private AbsenceStatus status;
    private AbsenceType type;
    private String userId;
}
