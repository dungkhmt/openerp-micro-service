package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.IStaffJobPositionPort;
import openerp.openerpresourceserver.constant.JobPositionStatus;
import openerp.openerpresourceserver.domain.exception.ApplicationException;
import openerp.openerpresourceserver.domain.model.JobPositionModel;
import openerp.openerpresourceserver.domain.model.StaffJobPositionModel;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffJobPositionEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.StaffJobPositionId;
import openerp.openerpresourceserver.infrastructure.output.persistence.projection.StaffJobPositionProjection;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.StaffJobPositionRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StaffJobPositionAdapter implements IStaffJobPositionPort {
    private static final Logger log = LoggerFactory.getLogger(StaffJobPositionAdapter.class);
    private final StaffJobPositionRepo staffJobPositionRepo;

    @Override
    public void assignJobPosition(String userLoginId, String jobPositionCode) {
        var currentJobOption = staffJobPositionRepo.findLatestJobByUserId(userLoginId);
        if (currentJobOption.isPresent()) {
            var currentJob = currentJobOption.get();
            if (currentJob.getJobPositionCode().equals(jobPositionCode)) {
                log.warn(String.format("Job position %s already assigned to user %s",
                        currentJob.getJobPositionCode(), userLoginId));
                return;
            }
        }
        var staffJobEntity = new StaffJobPositionEntity();
        var id = new StaffJobPositionId();
        id.setUserId(userLoginId);
        id.setPositionCode(jobPositionCode);
        id.setFromDate(LocalDate.now());
        staffJobEntity.setId(id);
        staffJobPositionRepo.save(staffJobEntity);
    }

    @Override
    public StaffJobPositionModel findCurrentJobPosition(String userLoginId) {
        var projection = staffJobPositionRepo.findLatestJobByUserId(userLoginId);
        return projection.map(this::toModel).orElse(null);
    }

    @Override
    public List<StaffJobPositionModel> findCurrentJobPositionIn(List<String> userLoginIds) {
        var projections = staffJobPositionRepo.findLatestPositionsByUserIds(userLoginIds);
        return toModels(projections);
    }

    @Override
    public List<StaffJobPositionModel> findJobPositionHistory(String userLoginId) {
        var projections = staffJobPositionRepo.findHistoryJobByUserId(userLoginId);
        return toModels(projections);
    }

    private StaffJobPositionModel toModel(StaffJobPositionProjection projection) {
        return StaffJobPositionModel.builder()
                .userLoginId(projection.getUserLoginId())
                .jobPosition(JobPositionModel.builder()
                        .code(projection.getJobPositionCode())
                        .name(projection.getJobPositionName())
                        .description(projection.getDescription())
                        .status(JobPositionStatus.valueOf(projection.getStatus()))
                        .build())
                .fromDate(projection.getFromDate())
                .thruDate(projection.getThruDate())
                .build();
    }

    private List<StaffJobPositionModel> toModels(List<StaffJobPositionProjection> projections) {
        return projections.stream().map(this::toModel).toList();
    }
}
