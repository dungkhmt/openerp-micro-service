package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.domain.model.StaffJobPositionModel;

import java.util.List;

public interface IStaffJobPositionPort {
    void assignJobPosition(String userLoginId, String jobPositionCode);
    StaffJobPositionModel findCurrentJobPosition(String userLoginId);
    List<StaffJobPositionModel> findCurrentJobPositionIn(List<String> userLoginIds);
    List<StaffJobPositionModel> findJobPositionHistory(String userLoginId);
}
