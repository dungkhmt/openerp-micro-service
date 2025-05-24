package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IJobPositionPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.service.JobPositionValidator;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.usecase_data.UpdateJobPosition;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
