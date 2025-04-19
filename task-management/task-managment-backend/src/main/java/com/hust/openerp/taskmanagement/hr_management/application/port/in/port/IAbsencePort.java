package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IAbsencePort {
    AbsenceModel createAbsence(AbsenceModel absenceModel);

    AbsenceModel updateAbsence(AbsenceModel absenceModel);

    void cancelAbsence(UUID id);

    AbsenceModel getAbsence(UUID id);

    List<AbsenceModel> getAbsences(List<String> userIds, LocalDate startDate, LocalDate endDate);
}
