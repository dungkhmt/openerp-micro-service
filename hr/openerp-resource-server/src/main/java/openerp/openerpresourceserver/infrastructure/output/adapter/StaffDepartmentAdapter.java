package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.IStaffDepartmentPort;
import openerp.openerpresourceserver.domain.exception.ApplicationException;
import openerp.openerpresourceserver.domain.model.StaffDepartmentModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffDepartmentEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffDepartmentId;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.StaffDepartmentRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StaffDepartmentAdapter implements IStaffDepartmentPort {
    private final StaffDepartmentRepo staffDepartmentRepo;

    @Override
    public StaffDepartmentModel assignDepartment(String userLoginId, String departmentCode) {
        var currentDepartmentOption = staffDepartmentRepo.findLatestDepartmentByUserId(userLoginId);
        if(currentDepartmentOption.isPresent()) {
            var currentJob = currentDepartmentOption.get();
            if(currentJob.getId().getDepartmentCode().equals(departmentCode)) {
                throw new ApplicationException(
                        ResponseCode.ASSIGN_JOB_POSITION_EXCEPTION,
                        String.format("Job position id %s already assigned to user %s",
                                currentJob.getId().getDepartmentCode() , userLoginId)
                );
            }
            currentJob.setThruDate(LocalDate.now());
            staffDepartmentRepo.save(currentJob);
        }
        var id = new StaffDepartmentId();
        id.setUserId(userLoginId);
        id.setDepartmentCode(departmentCode);
        id.setFromDate(LocalDate.now());
        var staffDepartmentEntity = new StaffDepartmentEntity();
        staffDepartmentEntity.setId(id);
        return toModel(staffDepartmentRepo.save(staffDepartmentEntity));
    }

    @Override
    public StaffDepartmentModel findCurrentDepartment(String userLoginId) {
        var currentJobOption = staffDepartmentRepo.findLatestDepartmentByUserId(userLoginId);
        return currentJobOption.map(this::toModel).orElse(null);
    }

    @Override
    public List<StaffDepartmentModel> findCurrentDepartmentIn(List<String> userLoginIds) {
        return toModels(
                staffDepartmentRepo.findLatestDepartmentsByUserIds(userLoginIds)
        );
    }

    @Override
    public List<StaffDepartmentModel> findDepartmentHistory(String userLoginId) {
        return toModels(
                staffDepartmentRepo.findHistoryDepartmentByUserId(userLoginId)
        );
    }

    private StaffDepartmentModel toModel(StaffDepartmentEntity entity) {
        return StaffDepartmentModel.builder()
                .departmentCode(entity.getId().getDepartmentCode())
                .userLoginId(entity.getId().getUserId())
                .fromDate(entity.getId().getFromDate())
                .thruDate(entity.getThruDate())
                .build();
    }

    private List<StaffDepartmentModel> toModels(Collection<StaffDepartmentEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
