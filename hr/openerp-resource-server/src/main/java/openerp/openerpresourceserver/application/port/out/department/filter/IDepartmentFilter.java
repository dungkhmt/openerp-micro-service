package openerp.openerpresourceserver.application.port.out.department.filter;

import openerp.openerpresourceserver.constant.DepartmentStatus;

public interface IDepartmentFilter {
    String getDepartmentCode();
    String getDepartmentName();
    DepartmentStatus getStatus();
}
