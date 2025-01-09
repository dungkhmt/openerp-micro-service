package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.filter.ICheckpointStaffFilter;
import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointStaffDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointStaffEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointStaffId;
import openerp.openerpresourceserver.infrastructure.output.persistence.projection.CheckpointStaffProjection;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.CheckpointStaffRepo;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.CheckpointStaffSpecification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CheckpointStaffAdapter implements ICheckpointStaffPort {
    private final CheckpointStaffRepo checkpointStaffRepo;

    @Override
    public List<CheckpointStaffModel> getAllCheckpointStaff(ICheckpointStaffFilter filter) {
        var spec = new CheckpointStaffSpecification(filter);
        return toModels(
                checkpointStaffRepo.findAll(spec)
        );
    }

    public List<CheckpointStaffDetailsModel> getCheckpointStaffDetails(UUID periodId, String userLoginId) {
        return toModels(
                checkpointStaffRepo.findCheckpointStaffDetails(periodId, userLoginId),
                periodId
        );
    }

    public List<CheckpointStaffDetailsModel> getCheckpointStaffDetailsIn(UUID periodId, List<String> userLoginIds) {
        return toModels(
                checkpointStaffRepo.findCheckpointStaffDetailsIn(periodId, userLoginIds),
                periodId
        );
    }

    @Override
    public Boolean existCheckpointStaff(UUID periodId) {
        return checkpointStaffRepo.existsById_CheckpointPeriodId(periodId);
    }

    @Override
    public void checkpointStaff(List<CheckpointStaffModel> checkpointStaffModels) {
        var entities = checkpointStaffModels.stream()
                .map(model -> {
                    var id = new CheckpointStaffId();
                    id.setCheckpointCode(model.getConfigureId());
                    id.setCheckpointPeriodId(model.getPeriodId());
                    id.setUserId(model.getUserId());
                    var entity = new CheckpointStaffEntity();
                    entity.setId(id);
                    entity.setPoint(model.getPoint());
                    entity.setCheckedByUserId(model.getCheckedByUserId());
                    return entity;
                }).toList();
        checkpointStaffRepo.saveAll(entities);
    }

    private CheckpointStaffDetailsModel toModel(
            CheckpointStaffProjection projection,
            UUID periodId
    ) {
        return CheckpointStaffDetailsModel.builder()
                .userId(projection.getUserId())
                .periodId(periodId)
                .point(projection.getPoint())
                .checkedByUserId(projection.getCheckedByUserId())
                .periodConfigure(CheckpointPeriodConfigureDetailsModel.builder()
                        .configureModel(CheckpointConfigureModel.builder()
                                .code(projection.getCheckpointCode())
                                .name(projection.getConfigureName())
                                .description(projection.getConfigureDescription())
                                .build())
                        .coefficient(projection.getCoefficient())
                        .status(Enum.valueOf(CheckpointPeriodConfigureStatus.class, projection.getPeriodStatus()))
                        .build())
                .build();
    }

    private List<CheckpointStaffDetailsModel> toModels(
            List<CheckpointStaffProjection> projections,
            UUID periodId
    ) {
        return projections.stream()
                .map(projection -> toModel(projection, periodId))
                .toList();
    }


    private CheckpointStaffModel toModel(CheckpointStaffEntity entity) {
        return CheckpointStaffModel.builder()
                .checkedByUserId(entity.getCheckedByUserId())
                .point(entity.getPoint())
                .configureId(entity.getId().getCheckpointCode())
                .periodId(entity.getId().getCheckpointPeriodId())
                .userId(entity.getId().getUserId())
                .build();
    }

    private List<CheckpointStaffModel> toModels(Collection<CheckpointStaffEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
