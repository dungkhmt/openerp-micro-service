package com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.usecase_data.FindStaff;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.PageWrapperUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;

import java.util.List;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class FindStaffHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<StaffModel, FindStaff>
{
    private final IStaffPort staffPort;

    @Override
    public void init() {
        register(FindStaff.class, this);
    }

    @Override
    public PageWrapper<StaffModel> handle(FindStaff useCase) {
        return staffPort.findStaff(useCase, useCase.getPageableRequest());
    }
}
