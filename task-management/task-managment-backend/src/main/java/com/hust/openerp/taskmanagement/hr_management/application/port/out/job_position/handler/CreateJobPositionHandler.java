package com.hust.openerp.taskmanagement.hr_management.application.port.out.job_position.handler;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IJobPositionPort;
import openerp.openerpresourceserver.application.port.out.code_generator.ICodeGeneratorService;
import openerp.openerpresourceserver.application.port.out.job_position.service.JobPositionValidator;
import openerp.openerpresourceserver.application.port.out.job_position.usecase_data.CreateJobPosition;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;
import org.springframework.beans.factory.annotation.Qualifier;

@DomainComponent
@Slf4j
public class CreateJobPositionHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<CreateJobPosition> {
    private final IJobPositionPort jobPositionPort;
    private final ICodeGeneratorService codeGenerator;
    private final JobPositionValidator validator;

    public CreateJobPositionHandler(
            IJobPositionPort jobPositionPort,
            @Qualifier("jobCodeGenerator") ICodeGeneratorService codeGenerator,
            JobPositionValidator validator) {
        this.jobPositionPort = jobPositionPort;
        this.validator = validator;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public void init() {
        register(CreateJobPosition.class,this);
    }

    @Override
    public void handle(CreateJobPosition useCase) {
        validator.validateJobName(useCase.getName());
        var model = useCase.toModel();
        var code = codeGenerator.generateCode(jobPositionPort);
        model.setCode(code);
        jobPositionPort.createJobPosition(model);
    }
}
