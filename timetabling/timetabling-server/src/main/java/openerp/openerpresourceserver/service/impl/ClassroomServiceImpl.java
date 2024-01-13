package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.exception.ClassroomNotFoundException;
import openerp.openerpresourceserver.mapper.ClassroomMapper;
import openerp.openerpresourceserver.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.repo.ClassroomRepo;
import openerp.openerpresourceserver.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void updateClassroom(ClassroomDto requestDto) {
        Long id = requestDto.getId();
        Classroom classroom = classroomRepo.findById(id).orElse(null);
        if (classroom == null) {
            throw new ClassroomNotFoundException("Not found semester with ID: " + id);
        }
        classroom.setClassroom(requestDto.getClassroom());
        classroom.setBuilding(requestDto.getBuilding());
        classroom.setQuantityMax(Long.parseLong(requestDto.getQuantityMax()));
        classroom.setDescription(requestDto.getDescription());
        classroom.setBuilding(requestDto.getBuilding());
        classroomRepo.save(classroom);
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
