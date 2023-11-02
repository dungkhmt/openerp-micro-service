package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.model.entity.ManagementCode;
import openerp.openerpresourceserver.repo.ManagementCodeRepo;
import openerp.openerpresourceserver.service.ManagementCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManagementCodeServiceImpl implements ManagementCodeService {

    @Autowired
    private ManagementCodeRepo managementCodeRepo;

    @Override
    public List<ManagementCode> getManagementCode() {
        return managementCodeRepo.findAll();
    }

    @Override
    public void updateManagementCode() {
        List<String> managementCodeDataList = managementCodeRepo.getManagementCode();
        if (!managementCodeDataList.isEmpty()) {
            managementCodeRepo.deleteAll();
        }
        List<ManagementCode> managementCodeList = new ArrayList<>();
        managementCodeDataList.forEach(el -> {
            ManagementCode managementCode = ManagementCode.builder()
                    .managementCode(el)
                    .build();
            managementCodeList.add(managementCode);
        });
        managementCodeRepo.saveAll(managementCodeList);
    }
}
