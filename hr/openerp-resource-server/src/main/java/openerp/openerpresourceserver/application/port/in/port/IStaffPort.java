package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.staff.filter.IStaffFilter;
import openerp.openerpresourceserver.domain.model.PageWrapper;
import openerp.openerpresourceserver.domain.model.StaffModel;
import openerp.openerpresourceserver.domain.model.IPageableRequest;

import java.util.List;

public interface IStaffPort extends ICodeGeneratorPort {
    StaffModel addStaff(StaffModel staff);

    StaffModel editStaff(StaffModel staff);

    StaffModel findByStaffCode(String staffCode);

    StaffModel findByUserLoginId(String userLoginId);

    PageWrapper<StaffModel> findStaff(IStaffFilter filter, IPageableRequest pageableRequest);

    List<StaffModel> findStaff(IStaffFilter filter);
}
