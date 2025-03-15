package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IJobPositionPort;
import openerp.openerpresourceserver.application.port.out.job_position.service.JobPositionValidator;
import openerp.openerpresourceserver.application.port.out.job_position.usecase_data.UpdateJobPosition;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdateJobPositionUseCaseHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateJobPosition>
{
    private final IJobPositionPort jobPositionPort;
    private final JobPositionValidator validator;

    @Override
    public void init() {
        register(UpdateJobPosition.class,this);
    }

    @Override
    public void handle(UpdateJobPosition useCase) {
        if(useCase.getName() != null){
            validator.validateJobName(useCase.getName());
        }
        var model = useCase.toModel();
        jobPositionPort.updateJobPosition(model);
    }
}
