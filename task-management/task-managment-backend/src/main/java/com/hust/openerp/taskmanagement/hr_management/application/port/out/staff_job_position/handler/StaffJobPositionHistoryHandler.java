package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffJobPositionPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_job_position.usecase_data.StaffJobPositionHistory;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffJobPositionModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class StaffJobPositionHistoryHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<StaffJobPositionModel, StaffJobPositionHistory>
{
    private final IStaffJobPositionPort staffJobPositionPort;

    @Override
    public void init() {
        register(StaffJobPositionHistory.class, this);
    }

    @Override
    public Collection<StaffJobPositionModel> handle(StaffJobPositionHistory useCase) {
        return staffJobPositionPort.findJobPositionHistory(useCase.getUserLoginId());
    }
}
