package openerp.openerpresourceserver.generaltimetabling.service.impl;

import openerp.openerpresourceserver.generaltimetabling.exception.ClassroomNotFoundException;
import openerp.openerpresourceserver.generaltimetabling.exception.ClassroomUsedException;
import openerp.openerpresourceserver.generaltimetabling.exception.NotFoundException;
import openerp.openerpresourceserver.generaltimetabling.mapper.ClassroomMapper;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Building;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassOpened;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import openerp.openerpresourceserver.generaltimetabling.repo.BuildingRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassOpenedRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassroomRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.GroupRepo;
import openerp.openerpresourceserver.generaltimetabling.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class ClassroomServiceImpl implements ClassroomService {
    @Autowired
    private GroupRepo groupRepo;
    @Autowired
    private ClassroomRepo classroomRepo;

    @Autowired
    private ClassOpenedRepo classOpenedRepo;

    @Autowired
    private BuildingRepo buildingRepo;

    @Autowired
    private ClassroomMapper classroomMapper;

    @Override
    public List<Classroom> getClassroom() {
        return classroomRepo.findAllWithBuilding();
    }

    @Override
    public List<String> getBuilding() {
        return classroomRepo.getBuilding();
    }

    @Override
    public void updateClassroom(ClassroomDto requestDto) {
        String id = requestDto.getId();
        Classroom classroom = classroomRepo.findById(id).orElse(null);
        if (!classroom.getClassroom().equals(requestDto.getClassroom())) {
            List<Classroom> classroomList = classroomRepo.getClassroomByClassroom(requestDto.getClassroom());
            if (!classroomList.isEmpty()) {
                throw new ClassroomUsedException("Phòng học " + requestDto.getClassroom() + " đã tồn tại!!");
            }
            List<ClassOpened> classOpenedList = classOpenedRepo.getAllByClassroom(classroom.getClassroom(), null);
            if (!classOpenedList.isEmpty()) {
                throw new ClassroomUsedException(
                        "Phòng học " + classroom.getClassroom() + " đang được sử dụng. Không thể sửa đổi!");
            }
        }

        String buildingName = requestDto.getBuilding();
        Building building = buildingRepo.findById(buildingName).orElseGet(() -> {
            Building newBuilding = Building.builder()
                    .id(buildingName) // Dùng `building name` làm `id`
                    .name(buildingName) // Dùng `building name` làm `name`
                    .build();
            return buildingRepo.save(newBuilding);
        });

        classroom.setId(requestDto.getClassroom());
        classroom.setClassroom(requestDto.getClassroom());
        classroom.setBuilding(building);
        classroom.setQuantityMax(Long.parseLong(requestDto.getQuantityMax()));
        classroom.setDescription(requestDto.getDescription());
        classroomRepo.save(classroom);
    }

    @Override
    public Classroom create(ClassroomDto classroomDto) {
        List<Classroom> classroomList = classroomRepo.getClassroomByClassroom(classroomDto.getClassroom());
        if (!classroomList.isEmpty()) {
            throw new ClassroomUsedException("Phòng học " + classroomDto.getClassroom() + " đã tồn tại!!");
        }
        Building building = buildingRepo.findById(classroomDto.getBuilding()).orElseThrow(() ->
                new NotFoundException("Tòa nhà " + classroomDto.getBuilding() + " không tồn tại!"));

        Classroom classroom = classroomMapper.mapDtoToEntity(classroomDto);
        classroom.setBuilding(building);
        classroomRepo.save(classroom);
        return classroom;
    }

    @Override
    public void deleteById(String id) {
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
    public void deleteByIds(List<String> ids) {
        ids.forEach(el -> {
            classroomRepo.deleteById(el);
        });
    }

    @Override
    public void clearAllClassRoom() {
        classOpenedRepo.deleteAll();
    }

    @Override
    public void clearAllClassRoomTimetable() {
        // Retrieve the list of ClassOpened entities
        List<ClassOpened> classOpenedList = classOpenedRepo.findAll();

        // Loop through the entities and clear the timetable fields
        for (ClassOpened classOpened : classOpenedList) {
            classOpened.setSecondClassroom(null);
            classOpened.setSecondStartPeriod(null);
            classOpened.setWeekday(null);
            classOpened.setStartPeriod(null);
            classOpened.setClassroom(null);
            classOpened.setSecondWeekday(null);
            classOpened.setIsSeparateClass(false);
        }

        // Save the modified entities
        classOpenedRepo.saveAll(classOpenedList);
    }

    @Override
    public List<Classroom> getMaxQuantityClassRoomByBuildings(String groupName, Integer maxAmount) {
        if (groupName == null || groupName.trim().isEmpty()) {
            return classroomRepo.findAll();
        }

        return classroomRepo.findByGroupNameAndOptionalMaxQuantity(groupName, maxAmount);
    }
}
