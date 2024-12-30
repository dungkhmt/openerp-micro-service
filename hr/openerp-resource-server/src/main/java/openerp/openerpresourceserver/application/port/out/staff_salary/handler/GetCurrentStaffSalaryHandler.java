package openerp.openerpresourceserver.application.port.out.staff_salary.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.application.port.in.port.IStaffSalaryPort;
import openerp.openerpresourceserver.application.port.out.staff_salary.usecase_data.GetCurrentStaffSalary;
import openerp.openerpresourceserver.domain.common.DomainComponent;
import openerp.openerpresourceserver.domain.common.usecase.ObservableUseCasePublisher;
import openerp.openerpresourceserver.domain.common.usecase.UseCaseHandler;
import openerp.openerpresourceserver.domain.model.StaffSalaryModel;

@DomainComponent
@Slf4j
@RequiredArgsConstructor
public class GetCurrentStaffSalaryHandler extends ObservableUseCasePublisher
        implements UseCaseHandler<StaffSalaryModel, GetCurrentStaffSalary> {
    private final IStaffSalaryPort staffSalaryPort;

    @Override
    public void init() {
        register(GetCurrentStaffSalary.class,this);
    }

    @Override
    public StaffSalaryModel handle(GetCurrentStaffSalary useCase) {
        return staffSalaryPort.findCurrentSalary(useCase.getUserLoginId());
    }
}
