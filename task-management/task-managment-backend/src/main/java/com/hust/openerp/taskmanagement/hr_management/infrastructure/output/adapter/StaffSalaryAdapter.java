package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.IStaffSalaryPort;
import com.hust.openerp.taskmanagement.hr_management.domain.model.StaffSalaryModel;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.StaffSalaryEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.StaffSalaryId;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.StaffSalaryRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StaffSalaryAdapter implements IStaffSalaryPort {
    private static final Logger log = LoggerFactory.getLogger(StaffSalaryAdapter.class);
    private final StaffSalaryRepo staffSalaryRepo;

    @Override
    public StaffSalaryModel updateSalary(StaffSalaryModel staffSalaryModel) {
        var currentSalaryOption = staffSalaryRepo.findLatestSalaryByUserId(staffSalaryModel.getUserLoginId());
        if(currentSalaryOption.isPresent()) {
            var currentSalary = currentSalaryOption.get();
            if(currentSalary.getSalaryType().equals(staffSalaryModel.getSalaryType())
                    && currentSalary.getSalary().equals(staffSalaryModel.getSalary()))
            {
                log.info("staff salary data not change");
                return toModel(currentSalary);
            }
            currentSalary.setThruDate(LocalDateTime.now());
            staffSalaryRepo.save(currentSalary);
        }
        var id = new StaffSalaryId();
        id.setUserId(staffSalaryModel.getUserLoginId());
        id.setFromDate(LocalDateTime.now());
        var staffSalaryEntity = new StaffSalaryEntity();
        staffSalaryEntity.setId(id);
        staffSalaryEntity.setSalary(staffSalaryModel.getSalary());
        staffSalaryEntity.setSalaryType(staffSalaryModel.getSalaryType());
        return toModel(staffSalaryRepo.save(staffSalaryEntity));
    }

    @Override
    public StaffSalaryModel findCurrentSalary(String userLoginId) {
        var currentSalary = staffSalaryRepo.findLatestSalaryByUserId(userLoginId);
        return currentSalary.map(this::toModel).orElse(null);
    }

    @Override
    public List<StaffSalaryModel> findCurrentSalaryIn(List<String> userLoginIds) {
        return toModels(
                staffSalaryRepo.findLatestSalariesByUserIds(userLoginIds)
        );
    }

    @Override
    public List<StaffSalaryModel> findSalaryHistory(String userLoginId) {
        return toModels(
                staffSalaryRepo.findHistorySalaryByUserId(userLoginId)
        );
    }

    private StaffSalaryModel toModel(StaffSalaryEntity entity) {
        return StaffSalaryModel.builder()
                .userLoginId(entity.getId().getUserId())
                .salaryType(entity.getSalaryType())
                .salary(entity.getSalary())
                .fromDate(entity.getId().getFromDate())
                .thruDate(entity.getThruDate())
                .build();
    }

    private List<StaffSalaryModel> toModels(Collection<StaffSalaryEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
