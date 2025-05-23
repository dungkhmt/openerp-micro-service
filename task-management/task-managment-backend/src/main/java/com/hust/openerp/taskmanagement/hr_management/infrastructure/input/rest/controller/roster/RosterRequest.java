package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RosterRequest {
    private String templateName;
    private List<ShiftDefinition> definedShifts;
    private Map<String, Object> activeHardConstraints;

    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> departmentCodes;
    private List<String> jobPositionCodes;
}