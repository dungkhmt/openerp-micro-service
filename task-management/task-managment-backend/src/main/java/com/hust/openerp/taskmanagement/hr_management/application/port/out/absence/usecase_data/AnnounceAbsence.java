package com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data;

import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceStatus;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.model.UseCase;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@Getter
@Setter
public class AnnounceAbsence implements UseCase {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private String userId;
    private AbsenceType type;

    public AbsenceModel toModel(){
        return AbsenceModel.builder()
            .date(date)
            .startTime(startTime)
            .endTime(endTime)
            .reason(reason)
            .userId(userId)
            .type(type)
            .status(AbsenceStatus.ACTIVE)
            .build();
    }
}
