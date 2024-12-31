package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointStaffPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.filter.ICheckpointStaffFilter;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointStaffEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointStaffId;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.CheckpointStaffRepo;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.CheckpointStaffSpecification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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

    private CheckpointStaffModel toModel(CheckpointStaffEntity entity) {
        return CheckpointStaffModel.builder()
                .build();
    }

    private List<CheckpointStaffModel> toModels(Collection<CheckpointStaffEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
