package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import lombok.Data;

@Data
public class ShiftDefinition {
    private String id;
    private String name;
    private String startTime; // HH:mm
    private String endTime;   // HH:mm
    private boolean isNightShift;
    private int minEmployees;
    private Integer maxEmployees; // null nếu không giới hạn
}