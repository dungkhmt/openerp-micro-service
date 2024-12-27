package openerp.openerpresourceserver.application.port.out.staff.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffPort;
import openerp.openerpresourceserver.application.port.out.staff.service.StaffValidator;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.EditStaff;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class EditStaffHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<EditStaff> {
    private final IStaffPort staffPort;
    private final StaffValidator staffValidator;

    @Override
    public void init() {
        register(EditStaff.class,this);
    }

    @Override
    public void handle(EditStaff useCase) {
        if(useCase.getFullName() != null){
            staffValidator.validateStaffName(useCase.getFullName());
        }
        staffPort.editStaff(useCase.toModel());
    }
}
