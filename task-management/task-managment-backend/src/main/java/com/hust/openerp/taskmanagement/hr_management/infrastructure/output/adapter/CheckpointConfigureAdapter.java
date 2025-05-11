package com.hust.openerp.taskmanagement.hr_management.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import com.hust.openerp.taskmanagement.hr_management.application.port.in.port.ICheckpointConfigurePort;
import com.hust.openerp.taskmanagement.hr_management.application.port.out.checkpoint_configure.filter.ICheckpointConfigureFilter;
import com.hust.openerp.taskmanagement.hr_management.domain.exception.ApplicationException;
import com.hust.openerp.taskmanagement.hr_management.domain.model.CheckpointConfigureModel;
import com.hust.openerp.taskmanagement.hr_management.domain.model.IPageableRequest;
import com.hust.openerp.taskmanagement.hr_management.domain.model.PageWrapper;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.entity.CheckpointConfigureEntity;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.repository.CheckpointConfigureRepo;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.specification.CheckpointConfigureSpecification;
import com.hust.openerp.taskmanagement.hr_management.infrastructure.output.persistence.utils.PageableUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CheckpointConfigureAdapter implements ICheckpointConfigurePort {
    private final CheckpointConfigureRepo checkpointConfigureRepo;

    @Override
    public CheckpointConfigureModel findByCode(String code) {
        return toModel(
                checkpointConfigureRepo.findById(code).orElseThrow(
                        () -> new ApplicationException(
                                ResponseCode.CHECKPOINT_CONFIGURE_NOT_EXISTED,
                                String.format("checkpoint configure not existed by code: %s", code)
                        )
                )
        );
    }

    @Override
    public List<CheckpointConfigureModel> findByCodeIn(List<String> codes) {
        return toModels(checkpointConfigureRepo.findByCheckpointCodeIn(codes));
    }

    @Override
    public CheckpointConfigureModel createCheckpointConfigure(CheckpointConfigureModel configure) {
        var entity = new CheckpointConfigureEntity();
        entity.setName(configure.getName());
        entity.setCheckpointCode(configure.getCode());
        entity.setDescription(configure.getDescription());
        entity.setStatus(configure.getStatus());
        return toModel(checkpointConfigureRepo.save(entity));
    }

    @Override
    public void updateCheckpointConfigure(CheckpointConfigureModel configure) {
        var entity = checkpointConfigureRepo.findById(configure.getCode())
                .orElseThrow(() -> new ApplicationException(
                                ResponseCode.CHECKPOINT_CONFIGURE_NOT_EXISTED,
                                String.format("Checkpoint configure not exist with code: %s", configure.getCode())
                        )
                );
        if(configure.getName() != null){
            entity.setName(configure.getName());
        }
        if(configure.getDescription() != null){
            entity.setDescription(configure.getDescription());
        }
        if(configure.getStatus() != null){
            entity.setStatus(configure.getStatus());
        }
        checkpointConfigureRepo.save(entity);
    }

    @Override
    public PageWrapper<CheckpointConfigureModel> getCheckpointConfigure(ICheckpointConfigureFilter filter, IPageableRequest request) {
        var pageable = PageableUtils.getPageable(request);
        var spec = new CheckpointConfigureSpecification(filter);
        var page = checkpointConfigureRepo.findAll(spec ,pageable);
        return PageWrapper.<CheckpointConfigureModel>builder()
                .pageInfo(PageableUtils.getPageInfo(page))
                .pageContent(toModels(page.getContent()))
                .build();
    }

    @Override
    public String findMaxCode(String prefix) {
        return checkpointConfigureRepo.findMaxCode(prefix);
    }

    private CheckpointConfigureModel toModel(CheckpointConfigureEntity entity) {
        return CheckpointConfigureModel.builder()
                .name(entity.getName())
                .code(entity.getCheckpointCode())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .build();
    }

    private List<CheckpointConfigureModel> toModels(Collection<CheckpointConfigureEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
