package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.mapper.ClassroomMapper;
import openerp.openerpresourceserver.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.model.entity.Semester;
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

    @Autowired
    private ClassroomMapper classroomMapper;

    @Override
    public List<Classroom> getClassroom() {
        return classroomRepo.findAll();
    }

    @Override
    public List<String> getBuilding() {
        return classroomRepo.getBuilding();
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

    @Override
    public Classroom create(ClassroomDto classroomDto) {
        Classroom classroom = classroomMapper.mapDtoToEntity(classroomDto);
        classroomRepo.save(classroom);
        return classroom;
    }

    @Override
    public void deleteById(Long id) {
        classroomRepo.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        ids.forEach(el -> {
            classroomRepo.deleteById(el);
        });
    }
}
