package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.OpenBatch;

import java.util.List;

public interface OpenBatchService {

    List<OpenBatch> getOpenBatch();

    void updateOpenBatch();
}
