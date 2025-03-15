package com.hust.openerp.taskmanagement.hr_management.application.port.in.port;

import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.filter.IDepartmentFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.model.DepartmentModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;

import java.util.List;

public interface IDepartmentPort extends ICodeGeneratorPort {
    DepartmentModel findByCode(String code);

    List<DepartmentModel> findByCodeIn(List<String> codes);

    void createDepartment(DepartmentModel department);
    void updateDepartment(DepartmentModel department);
    PageWrapper<DepartmentModel> getDepartment(IDepartmentFilter filter, IPageableRequest request);
}
