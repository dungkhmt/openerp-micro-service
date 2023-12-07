package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.ManagementCode;

import java.util.List;

public interface ManagementCodeService {

    List<ManagementCode> getManagementCode();

    void updateManagementCode();
}
