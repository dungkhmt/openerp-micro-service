package com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteShifts implements UseCase {
    private List<UUID> ids;
}
