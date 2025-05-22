package com.hust.openerp.taskmanagement.hr_management.application.port.out.shift.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.ShiftModel;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class CreateShifts implements UseCase {
    List<ShiftModel> shifts;
}
