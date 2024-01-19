package openerp.openerpresourceserver.service.impl;

import openerp.openerpresourceserver.exception.ClassroomNotFoundException;
import openerp.openerpresourceserver.exception.ClassroomUsedException;
import openerp.openerpresourceserver.exception.SemesterNotFoundException;
import openerp.openerpresourceserver.exception.SemesterUsedException;
import openerp.openerpresourceserver.mapper.ClassroomMapper;
import openerp.openerpresourceserver.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.model.entity.Semester;
import openerp.openerpresourceserver.repo.ClassOpenedRepo;
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
    private ClassOpenedRepo classOpenedRepo;

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
            throw new ClassroomNotFoundException("Không tìm thấy phòng học với ID: " + id);
        }
        if (!classroom.getClassroom().equals(requestDto.getClassroom())) {
            List<Classroom> classroomList = classroomRepo.getClassroomByClassroom(requestDto.getClassroom());
            if (!classroomList.isEmpty()) {
                throw new ClassroomUsedException("Phòng học " + requestDto.getClassroom() + " đã tồn tại!!");
            }
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
        List<Classroom> classroomList = classroomRepo.getClassroomByClassroom(classroomDto.getClassroom());
        if (!classroomList.isEmpty()) {
            throw new ClassroomUsedException("Phòng học " + classroomDto.getClassroom() + " đã tồn tại!!");
        }
        Classroom classroom = classroomMapper.mapDtoToEntity(classroomDto);
        classroomRepo.save(classroom);
        return classroom;
    }

    @Override
    public void deleteById(Long id) {
        Classroom classroom = classroomRepo.findById(id).orElse(null);
        if (classroom == null) {
            throw new ClassroomNotFoundException("Không tồn tại phòng học với ID: " + id);
        }
        List<ClassOpened> classOpenedList = classOpenedRepo.getAllByClassroom(classroom.getClassroom(), null);
        if (!classOpenedList.isEmpty()) {
            throw new ClassroomUsedException("Phòng học đang được sử dụng. Không thể xóa!");
        }
        classroomRepo.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        ids.forEach(el -> {
            classroomRepo.deleteById(el);
        });
    }
}
