package openerp.openerpresourceserver.application.port.out.job_position.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IJobPositionPort;
import openerp.openerpresourceserver.application.port.out.job_position.usecase_data.GetJobPosition;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.PageWrapperUseCaseHandler;
import openerp.openerpresourceserver.domain.model.JobPositionModel;
import openerp.openerpresourceserver.domain.model.PageWrapper;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetJobPositionHandler extends ObservableUseCasePublisher
        implements PageWrapperUseCaseHandler<JobPositionModel, GetJobPosition>
{
    private final IJobPositionPort departmentPort;

    @Override
    public void init() {
        register(GetJobPosition.class,this);
    }

    @Override
    public PageWrapper<JobPositionModel> handle(GetJobPosition useCase) {
        return departmentPort.getJobPosition(useCase, useCase.getPageableRequest());
    }
}
