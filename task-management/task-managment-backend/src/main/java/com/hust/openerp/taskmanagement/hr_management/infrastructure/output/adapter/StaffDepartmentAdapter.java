package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.IStaffDepartmentPort;
import openerp.openerpresourceserver.constant.DepartmentStatus;
import openerp.openerpresourceserver.domain.model.DepartmentModel;
import openerp.openerpresourceserver.domain.model.StaffDepartmentModel;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffDepartmentEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffDepartmentId;
import openerp.openerpresourceserver.infrastructure.output.persistence.projection.StaffDepartmentProjection;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.StaffDepartmentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StaffDepartmentAdapter implements IStaffDepartmentPort {
    private static final Logger log = LoggerFactory.getLogger(StaffDepartmentAdapter.class);
    private final StaffDepartmentRepo staffDepartmentRepo;

    @Override
    public void assignDepartment(String userLoginId, String departmentCode) {
        var currentDepartmentOption = staffDepartmentRepo.findLatestDepartmentByUserId(userLoginId);
        if (currentDepartmentOption.isPresent()) {
            var currentDepartment = currentDepartmentOption.get();
            if (currentDepartment.getId().getDepartmentCode().equals(departmentCode)) {
                log.warn(String.format("Job position id %s already assigned to user %s",
                        currentDepartment.getId().getDepartmentCode(), userLoginId));
                return;
            }
            currentDepartment.setThruDate(LocalDateTime.now());
            staffDepartmentRepo.save(currentDepartment);
        }
            var id = new StaffDepartmentId();
            id.setUserId(userLoginId);
            id.setDepartmentCode(departmentCode);
            id.setFromDate(LocalDateTime.now());
            var staffDepartmentEntity = new StaffDepartmentEntity();
            staffDepartmentEntity.setId(id);;
            staffDepartmentRepo.save(staffDepartmentEntity);
    }

    @Override
    public StaffDepartmentModel findCurrentDepartment(String userLoginId) {
        var projection = staffDepartmentRepo.findLatestProjectionDepartmentByUserId(userLoginId);
        return projection.map(this::toModel).orElse(null);
    }

    @Override
    public List<StaffDepartmentModel> findCurrentDepartmentIn(List<String> userLoginIds) {
        var projections = staffDepartmentRepo.findLatestDepartmentsByUserIds(userLoginIds);
        return toModels(projections);
    }

    @Override
    public List<StaffDepartmentModel> findDepartmentHistory(String userLoginId) {
        var projections = staffDepartmentRepo.findHistoryDepartmentByUserId(userLoginId);
        return toModels(projections);
    }

    private StaffDepartmentModel toModel(StaffDepartmentProjection projection) {
        return StaffDepartmentModel.builder()
                .userLoginId(projection.getUserLoginId())
                .departmentModel(DepartmentModel.builder()
                        .departmentCode(projection.getDepartmentCode())
                        .departmentName(projection.getDepartmentName())
                        .description(projection.getDescription())
                        .status(DepartmentStatus.valueOf(projection.getStatus()))
                        .build())
                .fromDate(projection.getFromDate())
                .thruDate(projection.getThruDate())
                .build();
    }

    private List<StaffDepartmentModel> toModels(List<StaffDepartmentProjection> projections) {
        return projections.stream().map(this::toModel).toList();
    }
}
