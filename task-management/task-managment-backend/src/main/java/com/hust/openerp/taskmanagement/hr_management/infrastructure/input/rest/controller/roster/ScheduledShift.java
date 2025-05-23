package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledShift {
    private String employeeId; // userLoginId
    private String employeeName;
    private String shiftName;
    private String date; // yyyy-MM-dd
    private String startTime; // HH:mm
    private String endTime;   // HH:mm
}