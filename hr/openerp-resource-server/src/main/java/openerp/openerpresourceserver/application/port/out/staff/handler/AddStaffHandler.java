package openerp.openerpresourceserver.application.port.out.staff.handler;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffPort;
import openerp.openerpresourceserver.application.port.out.code_generator.ICodeGeneratorService;
import openerp.openerpresourceserver.application.port.out.staff.service.StaffValidator;
import openerp.openerpresourceserver.application.port.out.staff.usecase_data.AddStaff;
import openerp.openerpresourceserver.application.port.out.staff_department.usecase_data.AssignDepartment;
import openerp.openerpresourceserver.application.port.out.staff_job_position.usecase_data.AssignJobPosition;
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
    @Transactional
    public void handle(AddStaff useCase) {
        var model = useCase.toModel();
        staffValidator.validateStaffName(model.getFullname());
        var code = codeGeneratorService.generateCode(staffPort);
        model.setStaffCode(code);
        var savedModel = staffPort.addStaff(model);
        if(useCase.getDepartmentCode() != null){
            addStaffDepartment(useCase.getDepartmentCode(), savedModel.getUserLoginId());
        }
        if(useCase.getJobPositionCode() != null){
            addStaffJobPosition(useCase.getJobPositionCode(), savedModel.getUserLoginId());
        }
    }

    private void addStaffDepartment(String departmentCode, String userLoginId){
        var assignDepartmentUseCase = AssignDepartment.builder()
                .departmentCode(departmentCode)
                .userLoginId(userLoginId)
                .build();
        publish(assignDepartmentUseCase);
    }

    private void addStaffJobPosition(String jobPositionCode, String userLoginId){
        var assignJobPosition = AssignJobPosition.builder()
                .jobPositionCode(jobPositionCode)
                .userLoginId(userLoginId)
                .build();
        publish(assignJobPosition);
    }
}
