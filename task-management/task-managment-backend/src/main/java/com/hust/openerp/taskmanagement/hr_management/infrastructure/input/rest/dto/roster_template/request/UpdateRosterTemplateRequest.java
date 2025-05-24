package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.roster_template.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data.UpdateRosterTemplate;
import com.hust.openerp.taskmanagement.hr_management.domain.model.RosterTemplateModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.controller.roster.ShiftDefinition;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateRosterTemplateRequest {
    @NotNull
    private UUID id;
    @NotNull
    private String templateName;
    @NotEmpty
    @NotNull
    private List<ShiftDefinition> definedShifts;
    @NotNull
    private Map<String, Object> activeHardConstraints;

    public UpdateRosterTemplate toUseCase(UUID id){
        return new UpdateRosterTemplate(
            RosterTemplateModel.builder()
                .id(id)
                .templateName(templateName)
                .definedShifts(definedShifts)
                .activeHardConstraints(activeHardConstraints)
                .build()
        );
    }
}
