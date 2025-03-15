package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffJobPositionPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data.GetAllCurrentStaffJobPosition;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffJobPositionModel;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetAllCurrentStaffJobPositionHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<StaffJobPositionModel, GetAllCurrentStaffJobPosition>
{
    private final IStaffJobPositionPort staffJobPositionPort;

    @Override
    public void init() {
        register(GetAllCurrentStaffJobPosition.class, this);
    }

    @Override
    public Collection<StaffJobPositionModel> handle(GetAllCurrentStaffJobPosition useCase) {
        return staffJobPositionPort.findCurrentJobPositionIn(useCase.getUserLoginIds());
    }
}
