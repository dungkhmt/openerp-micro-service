package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.ManagementCode;

import java.util.List;

public interface ManagementCodeService {

    List<ManagementCode> getManagementCode();

    void updateManagementCode();
}
