package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IDepartmentPort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.department.filter.IDepartmentFilter;
import com.hust.openerp.taskmanagement.hr_management.constant.DepartmentStatus;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.DepartmentModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.DepartmentEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.DepartmentRepo;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification.DepartmentSpecification;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils.PageableUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DepartmentAdapter implements IDepartmentPort {
    private final DepartmentRepo departmentRepo;

    @Override
    public DepartmentModel findByCode(String code) {
        return toModel(
                departmentRepo.findById(code).orElseThrow(
                        () -> new ApplicationException(
                                ResponseCode.DEPARTMENT_NOT_EXISTED,
                                String.format("Department not existed by code: %s", code)
                        )
                )
        );
    }

    @Override
    public List<DepartmentModel> findByCodeIn(List<String> codes) {
        return toModels(departmentRepo.findByDepartmentCodeIn(codes));
    }


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
        var departmentEntity = departmentRepo.findById(department.getDepartmentCode())
                .orElseThrow(() -> new ApplicationException(
                                ResponseCode.DEPARTMENT_NOT_EXISTED,
                                String.format("Department not exist with code: %s", department.getDepartmentCode())
                        )
                );
        if(department.getDepartmentName() != null){
            if(!departmentEntity.getDepartmentName().equals(department.getDepartmentName())){
                validateDepartmentName(department.getDepartmentName());
                departmentEntity.setDepartmentName(department.getDepartmentName());
            }
        }

        if(department.getStatus() != null){
            departmentEntity.setStatus(department.getStatus());
        }

        if(department.getDescription() != null){
            departmentEntity.setDescription(department.getDescription());
        }
        departmentRepo.save(departmentEntity);
    }

    @Override
    public PageWrapper<DepartmentModel> getDepartment(IDepartmentFilter filter, IPageableRequest request) {
        var pageable = PageableUtils.getPageable(request, "departmentCode");
        var spec = new DepartmentSpecification(filter);
        var page = departmentRepo.findAll(spec, pageable);
        return PageWrapper.<DepartmentModel>builder()
                .pageInfo(PageableUtils.getPageInfo(page))
                .pageContent(toModels(page.getContent()))
                .build();
    }


    private DepartmentModel toModel(DepartmentEntity department) {
        return DepartmentModel.builder()
                .departmentCode(department.getDepartmentCode())
                .departmentName(department.getDepartmentName())
                .description(department.getDescription())
                .status(department.getStatus())
                .build();
    }

    private List<DepartmentModel> toModels(Collection<DepartmentEntity> departments) {
        return departments.stream()
                .map(this::toModel)
                .toList();
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
