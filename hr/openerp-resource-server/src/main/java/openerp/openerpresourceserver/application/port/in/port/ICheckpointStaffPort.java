package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.checkpoint_staff.filter.ICheckpointStaffFilter;
import openerp.openerpresourceserver.domain.model.CheckpointStaffDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;

import java.util.List;
import java.util.UUID;

public interface ICheckpointStaffPort {

    List<CheckpointStaffModel> getAllCheckpointStaff(ICheckpointStaffFilter filter);

    void checkpointStaff(List<CheckpointStaffModel> checkpointStaffModels);

    List<CheckpointStaffDetailsModel> getCheckpointStaffDetails(UUID periodId, String userLoginId);

    List<CheckpointStaffDetailsModel> getCheckpointStaffDetailsIn(UUID periodId, List<String> userLoginIds);

    Boolean existCheckpointStaff(UUID periodId);
}
