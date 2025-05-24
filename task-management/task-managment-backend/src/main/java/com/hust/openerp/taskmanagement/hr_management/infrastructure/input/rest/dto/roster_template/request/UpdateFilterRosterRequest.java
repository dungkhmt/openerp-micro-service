package com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.roster_template.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data.UpdateRosterTemplate;
import com.hust.openerp.taskmanagement.hr_management.domain.model.RosterTemplateModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateFilterRosterRequest {
    private List<String> departmentFilter;
    private List<String> jobPositionFilter;

    public UpdateRosterTemplate toUseCase(UUID id){
        return new UpdateRosterTemplate(
            RosterTemplateModel.builder()
                .id(id)
                .departmentFilter(departmentFilter)
                .jobPositionFilter(jobPositionFilter)
                .build()
        );
    }
}
