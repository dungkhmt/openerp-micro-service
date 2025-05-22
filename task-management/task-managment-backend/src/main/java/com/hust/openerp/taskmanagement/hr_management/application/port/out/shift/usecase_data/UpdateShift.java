package com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class UpdateShift implements UseCase {
    private ShiftModel shiftModel;
}
