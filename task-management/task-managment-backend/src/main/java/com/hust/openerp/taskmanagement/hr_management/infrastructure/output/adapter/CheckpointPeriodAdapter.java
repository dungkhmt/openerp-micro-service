package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointPeriodPort;
import openerp.openerpresourceserver.application.port.out.checkpoint_period.filter.ICheckpointPeriodFilter;
import openerp.openerpresourceserver.domain.exception.ApplicationException;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodModel;
import openerp.openerpresourceserver.domain.model.IPageableRequest;
import openerp.openerpresourceserver.domain.model.PageWrapper;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointPeriodEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.CheckpointPeriodRepo;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.CheckpointPeriodSpecification;
import openerp.openerpresourceserver.infrastructure.output.persistence.utils.PageableUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CheckpointPeriodAdapter implements ICheckpointPeriodPort {
    private final CheckpointPeriodRepo checkpointPeriodRepo;


    @Override
    public CheckpointPeriodModel findByCode(UUID id) {
        return toModel(
                checkpointPeriodRepo.findById(id).orElseThrow(
                        () -> new ApplicationException(
                                ResponseCode.CHECKPOINT_CONFIGURE_NOT_EXISTED,
                                String.format("checkpoint configure not existed by id: %s", id)
                        )
                )
        );
    }

    @Override
    public List<CheckpointPeriodModel> findByCodeIn(List<UUID> ids) {
        return toModels(
                checkpointPeriodRepo.findByIdIn(ids)
        );
    }

    @Override
    public CheckpointPeriodModel createCheckpointPeriod(CheckpointPeriodModel period) {
        var entity = new CheckpointPeriodEntity();
        entity.setName(period.getName());
        entity.setDescription(period.getDescription());
        entity.setCheckpointDate(period.getCheckpointDate());
        entity.setCreatedByUserId(period.getCreatedByUserId());
        entity.setStatus(period.getStatus());
        return toModel(checkpointPeriodRepo.save(entity));
    }

    @Override
    public void updateCheckpointPeriod(CheckpointPeriodModel period) {
        var entity = checkpointPeriodRepo.findById(period.getId())
                .orElseThrow(() -> new ApplicationException(
                                ResponseCode.CHECKPOINT_PERIOD_NOT_EXISTED,
                                String.format("Checkpoint period not exist with id: %s", period.getId())
                        )
                );
        if(period.getName() != null){
            entity.setName(period.getName());
        }
        if(period.getDescription() != null){
            entity.setDescription(period.getDescription());
        }
        if(period.getCheckpointDate() != null){
            entity.setCheckpointDate(period.getCheckpointDate());
        }
        if(period.getStatus() != null){
            entity.setStatus(period.getStatus());
        }
        if(period.getCreatedByUserId() != null){
            entity.setCreatedByUserId(period.getCreatedByUserId());
        }
        checkpointPeriodRepo.save(entity);
    }

    @Override
    public PageWrapper<CheckpointPeriodModel> getCheckpointPeriod(ICheckpointPeriodFilter filter, IPageableRequest request) {
        var pageable = PageableUtils.getPageable(request, "checkpointDate");
        var spec = new CheckpointPeriodSpecification(filter);
        var page = checkpointPeriodRepo.findAll(spec ,pageable);
        return PageWrapper.<CheckpointPeriodModel>builder()
                .pageInfo(PageableUtils.getPageInfo(page))
                .pageContent(toModels(page.getContent()))
                .build();
    }

    private CheckpointPeriodModel toModel(CheckpointPeriodEntity entity) {
        return CheckpointPeriodModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .checkpointDate(entity.getCheckpointDate())
                .createdByUserId(entity.getCreatedByUserId())
                .status(entity.getStatus())
                .build();
    }

    private List<CheckpointPeriodModel> toModels(Collection<CheckpointPeriodEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
