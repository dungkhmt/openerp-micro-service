package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodConfigurePort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.filter.ICheckpointPeriodConfigureFilter;
import openerp.openerpresourceserver.constant.CheckpointConfigureStatus;
import openerp.openerpresourceserver.constant.CheckpointPeriodConfigureStatus;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureModel;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointPeriodConfigureEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointPeriodConfigureId;
import openerp.openerpresourceserver.infrastructure.output.persistence.projection.CheckpointPeriodConfigureProjection;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.CheckpointPeriodConfigureRepo;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.CheckpointPeriodConfigureSpecification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CheckpointPeriodConfigureAdapter implements ICheckpointPeriodConfigurePort {
    private final CheckpointPeriodConfigureRepo checkpointPeriodConfigureRepo;

    @Override
    public List<CheckpointPeriodConfigureDetailsModel> getPeriodConfigureDetails(UUID checkpointPeriodId) {

        return toModels(
                checkpointPeriodConfigureRepo.findConfigureDetails(checkpointPeriodId),
                checkpointPeriodId
        ) ;
    }

    @Override
    public List<CheckpointPeriodConfigureModel> getAllPeriodConfigure(ICheckpointPeriodConfigureFilter filter) {
        var spec = new CheckpointPeriodConfigureSpecification(filter);
        return toModels(
                checkpointPeriodConfigureRepo.findAll(spec)
        ) ;
    }

    @Override
    public void deleteAllPeriodConfigure(UUID periodId) {
        checkpointPeriodConfigureRepo.deleteAllByCheckpointPeriodId(periodId);
    }

    @Override
    public void createPeriodConfigure(List<CheckpointPeriodConfigureModel> models) {
        var entities = models.stream()
                .map(model -> {
                    var id = new CheckpointPeriodConfigureId();
                    id.setCheckpointPeriodId(model.getCheckpointPeriodId());
                    id.setCheckpointCode(model.getConfigureId());
                    var entity = new CheckpointPeriodConfigureEntity();
                    entity.setId(id);
                    entity.setStatus(model.getStatus() == null ? CheckpointPeriodConfigureStatus.ACTIVE : model.getStatus());
                    entity.setCoefficient(model.getCoefficient());
                    return entity;
                }).toList();
        checkpointPeriodConfigureRepo.saveAll(entities);
    }

    private CheckpointPeriodConfigureModel toModel(CheckpointPeriodConfigureEntity entity) {
        return CheckpointPeriodConfigureModel.builder()
                .checkpointPeriodId(entity.getId().getCheckpointPeriodId())
                .configureId(entity.getId().getCheckpointCode())
                .status(entity.getStatus())
                .coefficient(entity.getCoefficient())
                .build();
    }

    private CheckpointPeriodConfigureDetailsModel toModel(
            CheckpointPeriodConfigureProjection projection,
            UUID checkpointPeriodId
    ) {
        return CheckpointPeriodConfigureDetailsModel.builder()
                .configureModel(CheckpointConfigureModel.builder()
                        .code(projection.getCheckpointCode())
                        .name(projection.getName())
                        .status(CheckpointConfigureStatus.valueOf(projection.getConfigureStatus()))
                        .description(projection.getDescription())
                        .build())
                .checkpointPeriodId(checkpointPeriodId)
                .coefficient(projection.getCoefficient())
                .status(CheckpointPeriodConfigureStatus.valueOf(projection.getStatus()))
                .build();
    }

    private List<CheckpointPeriodConfigureDetailsModel> toModels(
            List<CheckpointPeriodConfigureProjection> projections,
            UUID checkpointPeriodId
    ) {
        return projections.stream()
                .map(projection -> toModel(projection, checkpointPeriodId))
                .toList();
    }



    private List<CheckpointPeriodConfigureModel> toModels(Collection<CheckpointPeriodConfigureEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
