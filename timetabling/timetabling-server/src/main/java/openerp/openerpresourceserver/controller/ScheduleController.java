package openerp.openerpresourceserver.controller;

import openerp.openerpresourceserver.model.entity.ClassCode;
import openerp.openerpresourceserver.model.entity.Institute;
import openerp.openerpresourceserver.model.entity.Schedule;
import openerp.openerpresourceserver.model.entity.Semester;
import openerp.openerpresourceserver.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/semesters")
    public ResponseEntity<List<Semester>> getAllSemester() {
        try {
            List<Semester> semesterList = scheduleService.getSemester();
            if (semesterList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(semesterList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/institutes")
    public ResponseEntity<List<Institute>> getAllInstitute() {
        try {
            List<Institute> instituteList = scheduleService.getInstitute();
            if (instituteList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(instituteList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/class-code")
    public ResponseEntity<List<ClassCode>> getAllClassCode() {
        try {
            List<ClassCode> classCodeList = scheduleService.getClassCode();
            if (classCodeList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(classCodeList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
