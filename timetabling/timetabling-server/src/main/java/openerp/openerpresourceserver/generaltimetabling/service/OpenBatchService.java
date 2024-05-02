package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.OpenBatch;

import java.util.List;

public interface OpenBatchService {

    List<OpenBatch> getOpenBatch();

    void updateOpenBatch();
}
