package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.entity.Module;

import java.util.List;

public interface ModuleService {

    List<Module> getModule();

    void updateModule();
}
