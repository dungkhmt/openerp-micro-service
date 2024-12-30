package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.domain.model.StaffSalaryModel;

import java.util.List;

public interface IStaffSalaryPort {
    StaffSalaryModel updateSalary(StaffSalaryModel model);
    StaffSalaryModel findCurrentSalary(String userLoginId);
    List<StaffSalaryModel> findCurrentSalaryIn(List<String> userLoginIds);
    List<StaffSalaryModel> findSalaryHistory(String userLoginId);
}
