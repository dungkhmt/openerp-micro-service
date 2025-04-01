package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.model.entity.Institute;
import openerp.openerpresourceserver.generaltimetabling.repo.InstituteRepo;
import openerp.openerpresourceserver.generaltimetabling.service.InstituteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InstituteServiceImpl implements InstituteService {

    @Autowired
    private InstituteRepo instituteRepo;

    @Override
    public List<Institute> getInstitute() {
        return instituteRepo.findAll();
    }

    @Override
    public void updateInstitute() {
        List<String> instituteDataList = instituteRepo.getInstitute();
        if (!instituteDataList.isEmpty()) {
            instituteRepo.deleteAll();
        }
        List<Institute> instituteList = new ArrayList<>();
        instituteDataList.forEach(el -> {
            Institute institute = Institute.builder()
                    .institute(el)
                    .build();
            instituteList.add(institute);
        });
        instituteRepo.saveAll(instituteList);
    }
}
