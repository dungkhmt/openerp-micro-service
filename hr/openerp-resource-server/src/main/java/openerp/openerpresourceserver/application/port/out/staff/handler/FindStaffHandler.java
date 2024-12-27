package openerp.openerpresourceserver.application.port.out.staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffPort;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.FindStaff;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.PageWrapperUseCaseHandler;
import openerp.openerpresourceserver.domain.model.PageWrapper;
import openerp.openerpresourceserver.domain.model.StaffModel;

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
        if(useCase.getPageableRequest() != null){
            return staffPort.findStaff(useCase, useCase.getPageableRequest());
        }
        //get all
        return PageWrapper.<StaffModel>builder()
                .pageContent(staffPort.findStaff(useCase))
                .pageInfo(null)
                .build();
    }
}
