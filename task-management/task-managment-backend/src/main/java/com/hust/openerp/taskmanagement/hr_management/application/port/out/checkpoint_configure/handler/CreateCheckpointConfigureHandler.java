package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.handler;

import lombok.extern.slf4j.Slf4j;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointConfigurePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.usecase_data.CreateCheckpointConfigure;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.code_generator.ICodeGeneratorService;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.UseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;
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
