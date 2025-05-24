package com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.handler;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointPeriodConfigurePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_period_configure.usecase_data.UpdateCheckpointPeriodConfigure;
import com.hust.openerp.taskmanagement.hr_management.domain.common.DomainComponent;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.ObservableUseCasePublisher;
import com.hust.openerp.taskmanagement.hr_management.domain.common.usecase.VoidUseCaseHandler;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class UpdatePeriodConfigureHandler extends ObservableUseCasePublisher
        implements VoidUseCaseHandler<UpdateCheckpointPeriodConfigure>
{
    private final ICheckpointPeriodConfigurePort periodConfigurePort;

    @Override
    public void init() {
        register(UpdateCheckpointPeriodConfigure.class,this);
    }

    @Override
    @Transactional
    public void handle(UpdateCheckpointPeriodConfigure useCase) {
        try{
            periodConfigurePort.deleteAllPeriodConfigure(useCase.getPeriodId());
            useCase.getConfigures().forEach(model ->
                model.setCheckpointPeriodId(useCase.getPeriodId())
            );
            periodConfigurePort.createPeriodConfigure(useCase.getConfigures());
        }
        catch(Exception e){
            log.error(e.getMessage(), e);
            throw new ApplicationException(
                    ResponseCode.EXCEPTION_ERROR,
                    "update checkpoint period configure error"
            );
        }

    }
}
