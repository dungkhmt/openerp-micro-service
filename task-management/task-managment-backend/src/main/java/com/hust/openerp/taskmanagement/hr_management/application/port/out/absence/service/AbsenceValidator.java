package com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.service;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IConfigPort;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.AbsenceModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AbsenceValidator {
    private final IConfigPort configPort;

    public void validate(AbsenceModel model){
        if(!model.getStartTime().isBefore(model.getEndTime())){
            throw new ApplicationException(ResponseCode.VALIDATE_ABSENCE_ERROR, "start time must be before end time");
        }
        var companyConfig = configPort.getCompanyConfig();
        var startDateTime = LocalDateTime.of(model.getDate(), model.getStartTime());
        LocalDateTime now = LocalDateTime.now();
        var duration = Duration.between(startDateTime, now);
        float timeBeforeLeave = duration.toSeconds() / 3600f;
        if(timeBeforeLeave < companyConfig.getHourBeforeAnnounceAbsence()){
            throw new ApplicationException(
                ResponseCode.VALIDATE_ABSENCE_ERROR,
                String.format("You must update at least %.1f hours before the start time."
                    , companyConfig.getHourBeforeAnnounceAbsence())
            );
        }
    }
}
