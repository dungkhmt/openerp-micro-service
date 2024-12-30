package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.checkpoint_staff.filter.ICheckpointStaffFilter;
import openerp.openerpresourceserver.domain.model.CheckpointStaffModel;

import java.util.List;

public interface ICheckpointStaffPort {

    List<CheckpointStaffModel> getAllCheckpointStaff(ICheckpointStaffFilter filter);

    void checkpointStaff(List<CheckpointStaffModel> checkpointStaffModels);
}
