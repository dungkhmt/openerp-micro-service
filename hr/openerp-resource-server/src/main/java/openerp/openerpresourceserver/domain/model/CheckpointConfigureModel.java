package openerp.openerpresourceserver.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.constant.CheckpointConfigureStatus;

@Getter
@Setter
@Builder
public class CheckpointConfigureModel {
    private String code;
    private String name;
    private String description;
    private CheckpointConfigureStatus status;
}
