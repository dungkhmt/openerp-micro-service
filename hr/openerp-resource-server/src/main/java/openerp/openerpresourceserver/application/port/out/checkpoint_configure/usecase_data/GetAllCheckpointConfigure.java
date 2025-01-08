package openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.filter.ICheckpointConfigureFilter;
import openerp.openerpresourceserver.constant.CheckpointConfigureStatus;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.IPageableRequest;

@Data
@Builder
@Getter
@Setter
public class GetAllCheckpointConfigure implements ICheckpointConfigureFilter, UseCase {
    private String name;
    private CheckpointConfigureStatus status;
    private IPageableRequest pageableRequest;
}
