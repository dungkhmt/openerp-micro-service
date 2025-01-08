package openerp.openerpresourceserver.generaltimetabling.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.generaltimetabling.exception.*;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.AutoMakeScheduleDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.FilterClassOpenedDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.MakeScheduleDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.UpdateClassOpenedDto;
import openerp.openerpresourceserver.generaltimetabling.model.entity.ClassOpened;
import openerp.openerpresourceserver.generaltimetabling.service.ClassOpenedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/class-opened")
public class ClassOpenedController {

    @Autowired
    private ClassOpenedService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<ClassOpened>> getAll(@RequestParam(required = false) String semester,
                                                    @RequestParam(required = false) String groupName) {
        try {
            List<ClassOpened> classOpenedList;
            if (semester != null) {
                classOpenedList = service.getBySemester(semester);
            } else classOpenedList = service.getAll();
            if (classOpenedList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(classOpenedList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/group-assign")
    public ResponseEntity<String> update(@Valid @RequestBody UpdateClassOpenedDto requestDto) {
        System.out.printf("assign group");
        try {
            List<ClassOpened> classOpenedList = service.updateClassOpenedList(requestDto);
            if (classOpenedList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete-ids")
    public ResponseEntity<Void> deleteByIds(@RequestParam List<Long> ids) {
        try {
            service.deleteByIds(ids);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search")
    public ResponseEntity<List<ClassOpened>> getClassOpenedByCondition(@Valid @RequestBody FilterClassOpenedDto requestDto) {
        try {
            List<ClassOpened> result = service.searchClassOpened(requestDto);
            if (result.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/separate-class")
    public ResponseEntity<String> separateClass(@RequestBody MakeScheduleDto requestDto) {
        try {
            service.setSeparateClass(requestDto.getId(), requestDto.getIsSeparateClass());
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (UnableSeparateClassException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/make-schedule")
    public ResponseEntity<String> makeSchedule(@Valid @RequestBody MakeScheduleDto requestDto) {
        try {
            service.makeSchedule(requestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UnableStartPeriodException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (ConflictScheduleException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/auto-make-schedule")
    public ResponseEntity<String> autoMakeSchedule(@Valid @RequestBody AutoMakeScheduleDto autoMakeScheduleDto) {
        try {
            service.automationMakeScheduleForCTTT(autoMakeScheduleDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotClassroomSuitableException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/auto-make-general-schedule")
    public ResponseEntity<String> autoMakeGeneralSchedule(@Valid @RequestBody AutoMakeScheduleDto autoMakeScheduleDto) {
        try {
            service.autoMakeGeneralSchedule(autoMakeScheduleDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotClassroomSuitableException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
