package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IJobPositionPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data.GetJobPosition;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.PageWrapperUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.JobPositionModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetJobPositionHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<JobPositionModel, GetJobPosition>
{
    private final IJobPositionPort jobPositionPort;

    @Override
    public void init() {
        register(GetJobPosition.class,this);
    }

    @Override
    public PageWrapper<JobPositionModel> handle(GetJobPosition useCase) {
        return jobPositionPort.getJobPosition(useCase, useCase.getPageableRequest());
    }
}
