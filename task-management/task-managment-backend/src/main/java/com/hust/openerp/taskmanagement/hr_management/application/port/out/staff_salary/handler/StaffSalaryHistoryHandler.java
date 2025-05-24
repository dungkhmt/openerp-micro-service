package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffSalaryPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff_salary.usecase_data.StaffSalaryHistory;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.CollectionUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffSalaryModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class StaffSalaryHistoryHandler extends ObservableUseCasePublisher
        implements CollectionUseCaseHandler<StaffSalaryModel, StaffSalaryHistory>
{
    private final IStaffSalaryPort staffSalaryPort;

    @Override
    public void init() {
        register(StaffSalaryHistory.class, this);
    }

    @Override
    public Collection<StaffSalaryModel> handle(StaffSalaryHistory useCase) {
        return staffSalaryPort.findSalaryHistory(useCase.getUserLoginId());
    }
}
