package openerp.openerpresourceserver.application.port.out.staff.handler;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffPort;
import openerp.openerpresourceserver.application.port.out.code_generator.ICodeGeneratorService;
import openerp.openerpresourceserver.application.port.out.staff.service.StaffValidator;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.AddStaff;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.VoidUseCaseHandler;
import org.springframework.beans.factory.annotation.Qualifier;

@DomainComponent
@Slf4j
public class AddStaffHandler extends ObservableUseCasePublisher implements VoidUseCaseHandler<AddStaff> {
    private final IStaffPort staffPort;
    private final StaffValidator staffValidator;
    private final ICodeGeneratorService codeGeneratorService;

    public AddStaffHandler(
            IStaffPort staffPort,
            StaffValidator staffValidator,
            @Qualifier("staffCodeGenerator") ICodeGeneratorService codeGeneratorService) {
        this.staffPort = staffPort;
        this.staffValidator = staffValidator;
        this.codeGeneratorService = codeGeneratorService;
    }

    @Override
    public void init() {
        register(AddStaff.class,this);
    }

    @Override
    public void handle(AddStaff useCase) {
        var model = useCase.toModel();
        staffValidator.validateStaffName(model.getFullname());
        var code = codeGeneratorService.generateCode(staffPort);
        model.setStaffCode(code);
        staffPort.addStaff(model);
    }
}
