package openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.domain.common.model.UseCase;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureModel;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class CheckpointStaff implements UseCase {
    private UUID periodId;
    private String userId;
    private String checkedByUserId;
    List<CheckpointStaffModel> models;
}
