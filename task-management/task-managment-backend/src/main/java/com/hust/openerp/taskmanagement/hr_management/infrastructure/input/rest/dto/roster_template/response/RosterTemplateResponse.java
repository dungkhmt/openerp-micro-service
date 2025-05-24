package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.roster_template.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.domain.model.RosterTemplateModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster.ShiftDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RosterTemplateResponse {
    private UUID id;
    private String templateName;
    private List<ShiftDefinition> definedShifts;
    private Map<String, Object> activeHardConstraints;
    private List<String> departmentFilter;
    private List<String> jobPositionFilter;

    public static RosterTemplateResponse fromModel(RosterTemplateModel model) {
        return RosterTemplateResponse.builder()
            .id(model.getId())
            .templateName(model.getTemplateName())
            .definedShifts(model.getDefinedShifts())
            .activeHardConstraints(model.getActiveHardConstraints())
            .departmentFilter(model.getDepartmentFilter())
            .jobPositionFilter(model.getJobPositionFilter())
            .build();
    }


    public static List<RosterTemplateResponse> fromModels(Collection<RosterTemplateModel> models) {
        return models.stream().map(RosterTemplateResponse::fromModel).toList();
    }
}
