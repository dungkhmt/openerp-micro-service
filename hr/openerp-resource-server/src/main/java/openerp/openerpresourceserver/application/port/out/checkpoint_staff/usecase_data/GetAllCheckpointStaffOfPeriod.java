package openerp.openerpresourceserver.application.port.out.checkpoint_staff.usecase_data;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.application.port.out.checkpoint_staff.filter.ICheckpointStaffFilter;
import openerp.openerpresourceserver.domain.common.model.UseCase;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class GetAllCheckpointStaffOfPeriod implements ICheckpointStaffFilter, UseCase {
    private List<String> userIds;
    private UUID periodId;
    private List<String> configureIds;

    @Override
    public String getUserId() {
        return null;
    }
}
