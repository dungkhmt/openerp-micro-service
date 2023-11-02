package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.repo.ClassroomRepo;
import openerp.openerpresourceserver.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClassroomServiceImpl implements ClassroomService {

    @Autowired
    private ClassroomRepo classroomRepo;

    @Override
    public List<Classroom> getClassroom() {
        return classroomRepo.findAll();
    }

    @Override
    public void updateClassroom() {
        List<String> classroomDataList = classroomRepo.getClassroom();
        if (!classroomDataList.isEmpty()) {
            classroomRepo.deleteAll();
        }
        List<Classroom> classroomList = new ArrayList<>();
        classroomDataList.forEach(el -> {
            Classroom classroom = Classroom.builder()
                    .classroom(el)
                    .build();
            classroomList.add(classroom);
        });
        classroomRepo.saveAll(classroomList);
    }

}
