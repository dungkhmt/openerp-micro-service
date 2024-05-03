package openerp.openerpresourceserver.generaltimetabling.service;

import openerp.openerpresourceserver.generaltimetabling.model.entity.Module;

import java.util.List;

public interface ModuleService {

    List<Module> getModule();

    void updateModule();
}
