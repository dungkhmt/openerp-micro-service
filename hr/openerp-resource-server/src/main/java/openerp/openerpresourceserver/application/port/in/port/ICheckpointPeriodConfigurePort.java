package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.checkpoint_period_configure.filter.ICheckpointPeriodConfigureFilter;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureDetailsModel;
import openerp.openerpresourceserver.domain.model.CheckpointPeriodConfigureModel;

import java.util.List;
import java.util.UUID;

public interface ICheckpointPeriodConfigurePort {
    List<CheckpointPeriodConfigureDetailsModel> getPeriodConfigureDetails(UUID checkpointPeriodId);
    List<CheckpointPeriodConfigureModel> getAllPeriodConfigure(ICheckpointPeriodConfigureFilter filter);
    void deleteAllPeriodConfigure(UUID periodId);
    void createPeriodConfigure(List<CheckpointPeriodConfigureModel> models);
}
