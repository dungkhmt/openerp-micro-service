package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.model.entity.Module;
import openerp.openerpresourceserver.repo.ModuleRepo;
import openerp.openerpresourceserver.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleRepo moduleRepo;

    @Override
    public List<Module> getModule() {
        return moduleRepo.findAll();
    }

    @Override
    public void updateModule() {
        List<String> moduleDataList = moduleRepo.getModule();
        if (!moduleDataList.isEmpty()) {
            moduleRepo.deleteAll();
        }
        List<Module> moduleList = new ArrayList<>();
        moduleDataList.forEach(el -> {
            String[] moduleData = el.split(",");
            Module module = Module.builder()
                    .moduleCode(moduleData[0])
                    .moduleName(moduleData[1])
                    .moduleNameByEnglish(moduleData[2])
                    .mass(moduleData[3])
                    .build();
            moduleList.add(module);
        });
        moduleRepo.saveAll(moduleList);
    }
}
