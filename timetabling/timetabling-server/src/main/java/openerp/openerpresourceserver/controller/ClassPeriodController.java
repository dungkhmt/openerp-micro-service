package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.model.dto.request.ClassPeriodDto;
import openerp.openerpresourceserver.model.dto.request.ClassroomDto;
import openerp.openerpresourceserver.model.entity.ClassPeriod;
import openerp.openerpresourceserver.model.entity.Classroom;
import openerp.openerpresourceserver.service.ClassPeriodService;
import openerp.openerpresourceserver.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/class-period")
public class ClassPeriodController {

    @Autowired
    private ClassPeriodService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<ClassPeriod>> getAllClassPeriod() {
        try {
            List<ClassPeriod> classPeriodList = service.getClassPeriod();
            if (classPeriodList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(classPeriodList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @GetMapping("/update")
//    public ResponseEntity<Void> updateClassroom() {
//        try {
//            service.updateClassroom();
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    @PostMapping("/create")
    public ResponseEntity<ClassPeriod> createNewClassPeriod(@Valid @RequestBody ClassPeriodDto classPeriodDto) {
        try {
            ClassPeriod classPeriod = service.create(classPeriodDto);
            return new ResponseEntity<>(classPeriod, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @DeleteMapping("/delete")
//    public ResponseEntity<Void> deleteById(@RequestParam Long id) {
//        try {
//            service.deleteById(id);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @DeleteMapping("/delete-ids")
//    public ResponseEntity<Void> deleteByIds(@RequestParam List<Long> ids) {
//        try {
//            service.deleteByIds(ids);
//            return new ResponseEntity<>(HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
