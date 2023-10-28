package openerp.openerpresourceserver.service.impl;

import com.fasterxml.jackson.databind.util.JSONPObject;
import openerp.openerpresourceserver.model.entity.ClassCode;
import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.model.entity.Institute;
import openerp.openerpresourceserver.model.entity.Semester;
import openerp.openerpresourceserver.repo.*;
import openerp.openerpresourceserver.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepo scheduleRepo;

    @Autowired
    private SemesterRepo semesterRepo;

    @Autowired
    private InstituteRepo instituteRepo;

    @Autowired
    private ClassCodeRepo classCodeRepo;

    @Autowired
    private ClassroomRepo classroomRepo;

    @Override
    public List<Semester> getSemester() {
        List<String> semesterDataList = scheduleRepo.getSemester();
        if (!semesterDataList.isEmpty()) {
            semesterRepo.deleteAll();
        }
        List<Semester> semesterList = new ArrayList<>();
        semesterDataList.forEach(el -> {
            Semester semester = Semester.builder()
                    .semester(el)
                    .build();
            semesterList.add(semester);
        });
        semesterRepo.saveAll(semesterList);
        return semesterRepo.findAll();
    }

    @Override
    public List<Institute> getInstitute() {
        List<String> instituteDataList = scheduleRepo.getInstitute();
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
        return instituteRepo.findAll();
    }

    @Override
    public List<ClassCode> getClassCode() {
        List<String> classCodeDataList = scheduleRepo.getClassCode();
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
        return classCodeRepo.findAll();
    }

    @Override
    public List<Classroom> getClassroom() {
        List<String> classroomDataList = scheduleRepo.getClassroom();
        if (!classroomDataList.isEmpty()) {
            classCodeRepo.deleteAll();
        }
        List<Classroom> classroomList = new ArrayList<>();
        classroomDataList.forEach(el -> {
            Classroom classroom = Classroom.builder()
                    .classroom(el)
                    .build();
            classroomList.add(classroom);
        });
        classroomRepo.saveAll(classroomList);
        return classroomRepo.findAll();
    }
}
