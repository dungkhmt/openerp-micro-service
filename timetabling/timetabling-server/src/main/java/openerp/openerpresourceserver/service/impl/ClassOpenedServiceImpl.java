package openerp.openerpresourceserver.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import openerp.openerpresourceserver.common.CommonUtil;
import openerp.openerpresourceserver.exception.ConflictScheduleException;
import openerp.openerpresourceserver.exception.UnableStartPeriodException;
import openerp.openerpresourceserver.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.model.dto.request.MakeScheduleDto;
import openerp.openerpresourceserver.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.model.entity.ClassOpened;
import openerp.openerpresourceserver.model.entity.Schedule;
import openerp.openerpresourceserver.repo.ClassOpenedRepo;
import openerp.openerpresourceserver.service.ClassOpenedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassOpenedServiceImpl implements ClassOpenedService {

    @Autowired
    private ClassOpenedRepo classOpenedRepo;

    @Autowired
    private EntityManager entityManager;

    public static final Long MAX_PERIOD = 6L;

    @Override
    public List<ClassOpened> getAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return classOpenedRepo.findAll(sort);
    }

    @Override
    public List<ClassOpened> updateClassOpenedList(UpdateClassOpenedDto requestDto) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<ClassOpened> classOpenedList = classOpenedRepo.getAllByIdIn(requestDto.getIds(), sort);
        String groupName = requestDto.getGroupName();
        classOpenedList.forEach(el -> {
            el.setGroupName(groupName);
        });
        classOpenedRepo.saveAll(classOpenedList);
        return classOpenedRepo.findAllById(requestDto.getIds());
    }

    @Override
    public List<ClassOpened> getBySemester(String semester) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return classOpenedRepo.getAllBySemester(semester, sort);
    }

    @Override
    public List<ClassOpened> getByGroupName(String groupName) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return classOpenedRepo.getAllByGroupName(groupName, sort);
    }

    @Override
    public List<Schedule> searchClassOpened(FilterClassOpenedDto searchDto) {
        StringBuilder jpql = this.getStringBuilder(searchDto);

        Query query = entityManager.createQuery(jpql.toString());

        // Execute the query and return the result list
        return CommonUtil.buildQueryForClassOpened(query, searchDto).getResultList();
    }

    private StringBuilder getStringBuilder(FilterClassOpenedDto searchDto) {
        StringBuilder jpql = new StringBuilder("SELECT s FROM ClassOpened s WHERE 1 = 1");

        return CommonUtil.appendAttributesForClassOpened(jpql, searchDto);
    }

    @Override
    public void makeSchedule(MakeScheduleDto requestDto) {
        ClassOpened classOpened = classOpenedRepo.findById(requestDto.getId()).orElse(null);
        if (classOpened == null) {
            return;
        }
        String startPeriod = requestDto.getStartPeriod();
        String weekday = requestDto.getWeekday();
        String classroom = requestDto.getClassroom();

        String subStartPeriod = startPeriod != null ? startPeriod : classOpened.getStartPeriod();
        String subClassroom = classroom != null ? classroom : classOpened.getClassroom();
        String subWeekday = weekday != null ? weekday : classOpened.getWeekday();

        if (subStartPeriod != null && subClassroom != null && subWeekday != null) {
            this.checkConflictSchedule(classOpened, requestDto);
        }

        if(subStartPeriod != null){
            classOpened.setStartPeriod(subStartPeriod);
        }

        classOpened.setWeekday(subWeekday);
        classOpened.setClassroom(subClassroom);
        classOpenedRepo.save(classOpened);
    }

    public void checkConflictSchedule(ClassOpened classOpened, MakeScheduleDto requestDto) {
        String classroomOfClass = requestDto.getClassroom() != null ? requestDto.getClassroom() : classOpened.getClassroom();
        String weekdayOfClass = requestDto.getWeekday() != null ? requestDto.getWeekday() : classOpened.getWeekday();
        String mass = classOpened.getMass();
        String crew = classOpened.getCrew();

        long startPeriod = Long.parseLong(requestDto.getStartPeriod() != null ?
                requestDto.getStartPeriod() : classOpened.getStartPeriod());
        long finishPeriod = this.calculateFinishPeriod(mass, startPeriod);

        List<ClassOpened> listClassOpened = classOpenedRepo
                .getAllByClassroomAndWeekdayAndCrewAndStartPeriodIsNotNullAndIdNot
                        (classroomOfClass, weekdayOfClass, crew, requestDto.getId());

        listClassOpened.forEach(el -> {
            String supMass = el.getMass();
            long existedStartPeriod = Long.parseLong(el.getStartPeriod());
            long existedFinishPeriod = this.calculateFinishPeriod(supMass, existedStartPeriod);

            if (startPeriod > existedStartPeriod) {
                if (startPeriod < existedFinishPeriod) {
                    throw new ConflictScheduleException("Trùng lịch với lớp: " + el.getModuleName());
                }
            } else {
                if (finishPeriod > existedStartPeriod) {
                    throw new ConflictScheduleException("Trùng lịch với lớp: " + el.getModuleName());
                }
            }
        });
    }

    public Long calculateFinishPeriod(String mass, Long startPeriod) {
        //a(b-c-d-e)
        String numbersString = mass.substring(2, mass.indexOf(')'));
        String[] numbersArray = numbersString.split("-");
        Long finishPeriod = startPeriod + Long.parseLong(numbersArray[0]) + Long.parseLong(numbersArray[1]) - 1;
        if (finishPeriod > MAX_PERIOD) {
            throw new UnableStartPeriodException("Tiết bắt đầu lỗi: tiết " + startPeriod);
        }
        return finishPeriod;
    }
}
