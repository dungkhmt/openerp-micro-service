package openerp.openerpresourceserver.infrastructure.output.adapter;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.application.port.in.port.ICheckpointConfigurePort;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.filter.ICheckpointConfigureFilter;
import openerp.openerpresourceserver.domain.exception.ApplicationException;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import openerp.openerpresourceserver.domain.model.IPageableRequest;
import openerp.openerpresourceserver.domain.model.PageWrapper;
import openerp.openerpresourceserver.infrastructure.input.rest.dto.common.response.resource.ResponseCode;
import openerp.openerpresourceserver.infrastructure.output.persistence.entity.CheckpointConfigureEntity;
import openerp.openerpresourceserver.infrastructure.output.persistence.repository.CheckpointConfigureRepo;
import openerp.openerpresourceserver.infrastructure.output.persistence.specification.CheckpointConfigureSpecification;
import openerp.openerpresourceserver.infrastructure.output.persistence.utils.PageableUtils;
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
    public void createCheckpointConfigure(CheckpointConfigureModel configure) {
        var entity = new CheckpointConfigureEntity();
        entity.setName(configure.getName());
        entity.setCheckpointCode(configure.getCode());
        entity.setDescription(configure.getDescription());
        entity.setStatus(configure.getStatus());
        checkpointConfigureRepo.save(entity);
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
                .build();
    }

    private List<CheckpointConfigureModel> toModels(Collection<CheckpointConfigureEntity> entities) {
        return entities.stream()
                .map(this::toModel)
                .toList();
    }
}
