package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.handler;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointConfigurePort;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data.CreateCheckpointConfigure;
import openerp.openerpresourceserver.application.port.out.code_generator.ICodeGeneratorService;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import org.springframework.beans.factory.annotation.Qualifier;

@DomainComponent
@Slf4j
public class CreateCheckpointConfigureHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<CheckpointConfigureModel, CreateCheckpointConfigure> {
    private final ICheckpointConfigurePort checkpointConfigurePort;
    private final ICodeGeneratorService codeGenerator;

    public CreateCheckpointConfigureHandler(
            ICheckpointConfigurePort checkpointConfigurePort,
            @Qualifier("checkpointConfigureCodeGenerator") ICodeGeneratorService codeGenerator) {
        this.checkpointConfigurePort = checkpointConfigurePort;
        this.codeGenerator = codeGenerator;
    }

    @Override
    public void init() {
        register(CreateCheckpointConfigure.class,this);
    }

    @Override
    public CheckpointConfigureModel handle(CreateCheckpointConfigure useCase) {
        var model = useCase.toModel();
        var code = codeGenerator.generateCode(checkpointConfigurePort);
        model.setCode(code);
        return checkpointConfigurePort.createCheckpointConfigure(model);
    }
}
