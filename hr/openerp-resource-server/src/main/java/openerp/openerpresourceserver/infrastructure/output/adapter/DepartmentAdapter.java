package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.IDepartmentPort;
import openerp.openerpresourceserver.application.port.out.department.filter.IDepartmentFilter;
import openerp.openerpresourceserver.constant.DepartmentStatus;
import openerp.openerpresourceserver.domain.exception.ApplicationException;
import openerp.openerpresourceserver.domain.model.DepartmentModel;
import openerp.openerpresourceserver.domain.model.IPageableRequest;
import openerp.openerpresourceserver.domain.model.PageWrapper;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.DepartmentEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.DepartmentRepo;

@RequiredArgsConstructor
public class DepartmentAdapter implements IDepartmentPort {
    private final DepartmentRepo departmentRepo;

    @Override
    public void createDepartment(DepartmentModel department) {
        validateCreateDepartment(department);
        var departmentEntity = new DepartmentEntity();
        departmentEntity.setDepartmentCode(department.getDepartmentCode());
        departmentEntity.setDepartmentName(department.getDepartmentName());
        departmentEntity.setDescription(department.getDescription());
        departmentEntity.setStatus(DepartmentStatus.ACTIVE);
        departmentRepo.save(departmentEntity);
    }


    @Override
    public void updateDepartment(DepartmentModel department) {
        validateDepartmentName(department.getDepartmentName());
        var departmentEntity = departmentRepo.findById(department.getDepartmentCode())
                .orElseThrow(() -> new ApplicationException(
                                ResponseCode.DEPARTMENT_NOT_EXISTED,
                                String.format("Department not exist with code: %s", department.getDepartmentCode())
                        )
                );
        departmentEntity.setDepartmentName(department.getDepartmentName());
        departmentEntity.setDescription(department.getDescription());
        departmentEntity.setStatus(department.getStatus());
        departmentRepo.save(departmentEntity);
    }

    @Override
    public PageWrapper<DepartmentModel> getDepartment(IDepartmentFilter filter, IPageableRequest request) {
        return null;
    }


    private DepartmentModel toModel(DepartmentEntity department) {
        return DepartmentModel.builder()
                .departmentCode(department.getDepartmentCode())
                .departmentName(department.getDepartmentName())
                .description(department.getDescription())
                .status(department.getStatus())
                .build();
    }

    private void validateCreateDepartment(DepartmentModel department) {
        if(departmentRepo.existsById(department.getDepartmentCode())){
            throw new ApplicationException(
                    ResponseCode.DEPARTMENT_CODE_EXISTED,
                    String.format("Department code already exists: %s", department.getDepartmentCode())
            );
        }
        validateDepartmentName(department.getDepartmentName());
    }

    private void validateDepartmentName(String name) {
        if(departmentRepo.existsByDepartmentName(name)){
            throw new ApplicationException(
                    ResponseCode.DEPARTMENT_NAME_EXISTED,
                    String.format("Department name already exists: %s", name)
            );
        }
    }

    @Override
    public String findMaxCode(String prefix) {
        return departmentRepo.findMaxDepartmentCode(prefix);
    }
}
