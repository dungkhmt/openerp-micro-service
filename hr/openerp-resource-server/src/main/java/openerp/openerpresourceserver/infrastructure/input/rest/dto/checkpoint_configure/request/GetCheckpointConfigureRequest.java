package openerp.openerpresourceserver.infrastructure.input.rest.dto.checkpoint_configure.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_configure.usecase_data.GetCheckpointConfigure;
import openerp.openerpresourceserver.constant.CheckpointConfigureStatus;
import openerp.openerpresourceserver.domain.model.IPageableRequest;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GetCheckpointConfigureRequest {
    private String name;
    private CheckpointConfigureStatus status;
    private IPageableRequest pageableRequest;

    public GetCheckpointConfigure toUseCase(){
        return GetCheckpointConfigure.builder()
                .name(name)
                .status(status)
                .pageableRequest(pageableRequest)
                .build();
    }
}
