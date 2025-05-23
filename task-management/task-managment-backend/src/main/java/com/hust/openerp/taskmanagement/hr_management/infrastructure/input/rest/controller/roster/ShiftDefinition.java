package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ShiftDefinition {
    private String id;
    private String name;
    private String startTime; // HH:mm
    private String endTime;   // HH:mm
    private Boolean isNightShift;
    private Integer minEmployees;
    private Integer maxEmployees; // null nếu không giới hạn
}