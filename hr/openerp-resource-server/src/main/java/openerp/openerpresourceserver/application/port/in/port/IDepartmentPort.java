package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.department.filter.IDepartmentFilter;
import openerp.openerpresourceserver.domain.model.DepartmentModel;
import openerp.openerpresourceserver.domain.model.IPageableRequest;
import openerp.openerpresourceserver.domain.model.PageWrapper;

import java.util.List;

public interface IDepartmentPort extends ICodeGeneratorPort {
    DepartmentModel findByCode(String code);

    List<DepartmentModel> findByCodeIn(List<String> codes);

    void createDepartment(DepartmentModel department);
    void updateDepartment(DepartmentModel department);
    PageWrapper<DepartmentModel> getDepartment(IDepartmentFilter filter, IPageableRequest request);
}
