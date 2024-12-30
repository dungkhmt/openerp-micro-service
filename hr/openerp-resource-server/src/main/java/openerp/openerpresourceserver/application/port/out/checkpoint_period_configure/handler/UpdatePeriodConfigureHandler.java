package openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodConfigurePort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.usecase_data.UpdateCheckpointPeriodConfigure;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;
import openerp.openerpresourceserver.domain.exception.ApplicationException;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;

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
            useCase.getModels().forEach(model ->
                model.setCheckpointPeriodId(useCase.getPeriodId())
            );
            periodConfigurePort.createPeriodConfigure(useCase.getModels());
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
