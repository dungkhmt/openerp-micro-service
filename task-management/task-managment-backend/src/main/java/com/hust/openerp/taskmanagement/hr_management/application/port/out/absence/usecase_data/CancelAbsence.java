package com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelAbsence implements UseCase {
    private UUID id;
    private String userId;
}
