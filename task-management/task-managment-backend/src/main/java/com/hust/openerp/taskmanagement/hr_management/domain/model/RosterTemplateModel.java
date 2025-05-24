package com.hust.openerp.taskmanagement.hr_management.domain.model;

import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster.ShiftDefinition;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RosterTemplateModel {
    private UUID id;
    private String templateName;
    private List<ShiftDefinition> definedShifts;
    private Map<String, Object> activeHardConstraints;
    private List<String> departmentFilter;
    private List<String> jobPositionFilter;
}
