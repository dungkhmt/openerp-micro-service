package com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.RosterTemplateModel;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CreateRosterTemplate implements UseCase {
    private RosterTemplateModel rosterTemplateModel;
}
