package com.hust.openerp.taskmanagement.hr_management.application.port.out.roster_template.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetRosterTemplate implements UseCase {
    private UUID id;
}
