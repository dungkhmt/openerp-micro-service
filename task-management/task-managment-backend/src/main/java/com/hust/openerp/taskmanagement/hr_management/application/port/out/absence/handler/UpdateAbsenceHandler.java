package com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IAbsencePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IConfigPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.service.AbsenceValidator;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.absence.usecase_data.UpdateAbsence;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.leave_hours.usecase_data.UpdateAbsenceLeaveHours;
import com.hust.openerp.taskmanagement.hr_management.constant.AbsenceType;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateAbsenceHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<UpdateAbsence> {
    private final IAbsencePort absencePort;
    private final AbsenceValidator absenceValidator;
    private final IConfigPort configPort;

    @Override
    public void init() {
        register(UpdateAbsence.class,this);
    }

    @Override
    public void handle(UpdateAbsence useCase) {
        var absence = absencePort.getAbsence(useCase.getId());
        if(!absence.getUserId().equals(useCase.getUserId())) {
            throw new ApplicationException(ResponseCode.UNAUTHORIZED, "User not authorized");
        }
        var companyConfig = configPort.getCompanyConfig();
        var model = useCase.toModel();
        absenceValidator.validate(model);
        var paidLeaveTimeByHours = absence.getType() == AbsenceType.UNPAID_LEAVE ? 0.0f : absence.getDurationTimeAbsence(companyConfig);
        var updatedPaidLeaveTimeByHours = useCase.getType() == AbsenceType.UNPAID_LEAVE ? 0.0f : model.getDurationTimeAbsence(companyConfig);
        var paidLeaveAddTime = updatedPaidLeaveTimeByHours - paidLeaveTimeByHours;
        if(paidLeaveAddTime > 0) {
            absenceValidator.validateLeaveHour(absence.getUserId(), paidLeaveAddTime);
        }
        var absenceUpdated = absencePort.updateAbsence(model);
        if(paidLeaveAddTime == 0.0f) return;
        publish(
            UpdateAbsenceLeaveHours.of(
                paidLeaveTimeByHours,
                updatedPaidLeaveTimeByHours,
                absence.getUserId()
            )
        );
    }
}
