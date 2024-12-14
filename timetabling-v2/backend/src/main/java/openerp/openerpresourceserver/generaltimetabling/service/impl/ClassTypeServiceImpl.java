package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassType;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassTypeRepo;
import openerp.openerpresourceserver.generaltimetabling.service.ClassTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClassTypeServiceImpl implements ClassTypeService {

    @Autowired
    private ClassTypeRepo classTypeRepo;

    @Override
    public List<ClassType> getClassType() {
        return classTypeRepo.findAll();
    }

    @Override
    public void updateClassType() {
        List<String> classTypeDataList = classTypeRepo.getClassType();
        if (!classTypeDataList.isEmpty()) {
            classTypeRepo.deleteAll();
        }
        List<ClassType> classTypeList = new ArrayList<>();
        classTypeDataList.forEach(el -> {
            ClassType classCode = ClassType.builder()
                    .classType(el)
                    .build();
            classTypeList.add(classCode);
        });
        classTypeRepo.saveAll(classTypeList);
    }
}
