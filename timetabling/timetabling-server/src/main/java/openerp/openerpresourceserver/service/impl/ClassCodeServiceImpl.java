package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.model.entity.ClassCode;
import openerp.openerpresourceserver.repo.ClassCodeRepo;
import openerp.openerpresourceserver.service.ClassCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClassCodeServiceImpl implements ClassCodeService {

    @Autowired
    private ClassCodeRepo classCodeRepo;

    @Override
    public List<ClassCode> getClassCode() {
        return classCodeRepo.findAll();
    }

    @Override
    public void updateClassCode() {
        List<String> classCodeDataList = classCodeRepo.getClassCode();
        if (!classCodeDataList.isEmpty()) {
            classCodeRepo.deleteAll();
        }
        List<ClassCode> classCodeList = new ArrayList<>();
        classCodeDataList.forEach(el -> {
            String[] classcode = el.split(",");
            ClassCode classCode = ClassCode.builder()
                    .classCode(classcode[0])
                    .bundleClassCode(classcode[1])
                    .build();
            classCodeList.add(classCode);
        });
        classCodeRepo.saveAll(classCodeList);
    }
}
