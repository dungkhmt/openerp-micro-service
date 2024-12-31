package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.IStaffJobPositionPort;
import openerp.openerpresourceserver.domain.exception.ApplicationException;
import openerp.openerpresourceserver.domain.model.StaffJobPositionModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffJobPositionEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffJobPositionId;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.StaffJobPositionRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StaffJobPositionAdapter implements IStaffJobPositionPort {
    private final StaffJobPositionRepo staffJobPositionRepo;

    @Override
    public StaffJobPositionModel assignJobPosition(String userLoginId, String jobPositionCode) {
        var currentJobOption = staffJobPositionRepo.findLatestJobByUserId(userLoginId);
        if(currentJobOption.isPresent()) {
            var currentJob = currentJobOption.get();
            if(currentJob.getId().getPositionCode().equals(jobPositionCode)) {
                throw new ApplicationException(
                        ResponseCode.ASSIGN_JOB_POSITION_EXCEPTION,
                        String.format("Job position id %s already assigned to user %s",
                                currentJob.getId().getPositionCode() , userLoginId)
                );
            }
            currentJob.setThruDate(LocalDate.now());
            staffJobPositionRepo.save(currentJob);
        }
        var id = new StaffJobPositionId();
        id.setUserId(userLoginId);
        id.setPositionCode(jobPositionCode);
        id.setFromDate(LocalDate.now());
        var staffJobEntity = new StaffJobPositionEntity();
        staffJobEntity.setId(id);
        return toModel(staffJobPositionRepo.save(staffJobEntity));
    }

    @Override
    public StaffJobPositionModel findCurrentJobPosition(String userLoginId) {
        var currentJobOption = staffJobPositionRepo.findLatestJobByUserId(userLoginId);
        return currentJobOption.map(this::toModel).orElse(null);
    }

    @Override
    public List<StaffJobPositionModel> findCurrentJobPositionIn(List<String> userLoginIds) {
        return toModels(
                staffJobPositionRepo.findLatestPositionsByUserIds(userLoginIds)
        );
    }

    @Override
    public List<StaffJobPositionModel> findJobPositionHistory(String userLoginId) {
        return toModels(
                staffJobPositionRepo.findHistoryJobByUserId(userLoginId)
        );
    }

    private StaffJobPositionModel toModel(StaffJobPositionEntity entity) {
        return StaffJobPositionModel.builder()
                .jobPositionCode(entity.getId().getPositionCode())
                .userLoginId(entity.getId().getUserId())
                .fromDate(entity.getId().getFromDate())
                .thruDate(entity.getThruDate())
                .build();
    }

    private List<StaffJobPositionModel> toModels(Collection<StaffJobPositionEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
