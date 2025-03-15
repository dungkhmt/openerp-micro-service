package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.staff.filter.IStaffFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;

import java.util.List;

public interface IStaffPort extends ICodeGeneratorPort {
    StaffModel addStaff(StaffModel staff);

    StaffModel editStaff(StaffModel staff);

    StaffModel findByStaffCode(String staffCode);

    StaffModel findByUserLoginId(String userLoginId);

    PageWrapper<StaffModel> findStaff(IStaffFilter filter, IPageableRequest pageableRequest);

    List<StaffModel> findStaff(IStaffFilter filter);
}
