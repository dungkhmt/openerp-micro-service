package openerp.openerpresourceserver.application.port.in.port;

import openerp.openerpresourceserver.application.port.out.checkpoint_configure.filter.ICheckpointConfigureFilter;
import openerp.openerpresourceserver.domain.model.CheckpointConfigureModel;
import openerp.openerpresourceserver.domain.model.IPageableRequest;
import openerp.openerpresourceserver.domain.model.PageWrapper;

import java.util.List;

public interface ICheckpointConfigurePort extends ICodeGeneratorPort{
    CheckpointConfigureModel findByCode(String code);
    List<CheckpointConfigureModel> findByCodeIn(List<String> codes);
    void createCheckpointConfigure(CheckpointConfigureModel configure);
    void updateCheckpointConfigure(CheckpointConfigureModel configure);
    PageWrapper<CheckpointConfigureModel> getCheckpointConfigure
            (ICheckpointConfigureFilter filter, IPageableRequest request);
}
