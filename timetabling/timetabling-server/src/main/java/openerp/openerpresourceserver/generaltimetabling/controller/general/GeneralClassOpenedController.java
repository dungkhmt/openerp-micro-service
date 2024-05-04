package openerp.openerpresourceserver.generaltimetabling.controller.general;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.exception.ConflictScheduleException;
import openerp.openerpresourceserver.generaltimetabling.exception.InvalidClassStudentQuantityException;
import openerp.openerpresourceserver.generaltimetabling.exception.NotFoundException;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateClassesToNewGroupRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.ResetScheduleRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.generaltimetabling.service.GeneralClassOpenedService;

@RestController
@RequestMapping("/general-classes")
@AllArgsConstructor
@Log4j2
public class GeneralClassOpenedController {
    private GeneralClassOpenedService gService;
    @ExceptionHandler(ConflictScheduleException.class)
    public ResponseEntity scheduleConflict(ConflictScheduleException e) {
        return ResponseEntity.status(410).body(e.getCustomMessage());
    }
    @ExceptionHandler(InvalidClassStudentQuantityException.class)
    public ResponseEntity scheduleConflict(InvalidClassStudentQuantityException e) {
        return ResponseEntity.status(410).body(e.getCustomMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity notFoundSolution(NotFoundException e) {
        return ResponseEntity.status(410).body(e.getCustomMessage());
    }

    @GetMapping("/")
    public ResponseEntity<List<GeneralClassOpened>> getClasses(@RequestParam("semester") String semester, @RequestParam("groupName") String groupName) {
        try {
            List<GeneralClassOpened> generalClassOpenedList = gService.getGeneralClasses(semester, groupName);
            return ResponseEntity.ok(generalClassOpenedList);
        } catch(Exception e) {
            System.err.println(e);
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    
    @PostMapping("/update-class")
    public ResponseEntity<GeneralClassOpened> updateClass(@RequestBody UpdateGeneralClassRequest request) {
        GeneralClassOpened updatedGeneralClass= gService.updateGeneralClass(request);
        if(updatedGeneralClass == null) throw new RuntimeException("General Class was null");
        return ResponseEntity.ok().body(updatedGeneralClass);
    }

    @PostMapping("/update-class-schedule")
    public ResponseEntity<GeneralClassOpened> updateClassSchedule(@RequestParam("semester")String semester, @RequestBody UpdateGeneralClassScheduleRequest request ) {
        GeneralClassOpened updatedGeneralClass= gService.updateGeneralClassSchedule(semester, request);
        if(updatedGeneralClass == null) throw new RuntimeException("General Class was null");
        return ResponseEntity.ok().body(updatedGeneralClass);
    }

    @PostMapping("/update-classes-group")
    public ResponseEntity updateClassesGroup(@RequestBody UpdateClassesToNewGroupRequest request) {
        try{
            if(request.getPriorityBuilding() == null) return ResponseEntity.ok(gService.addClassesToCreatedGroup(request.getIds(), request.getGroupName()));
            return ResponseEntity.ok(gService.addClassesToNewGroup(request.getIds(),request.getGroupName(),request.getPriorityBuilding()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/")
    public ResponseEntity deleteClassesBySemester(@RequestParam("semester") String semester) {
        gService.deleteClassesBySemester(semester);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/export-excel")
    public ResponseEntity exportExcel(@RequestParam("semester") String semester) {
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/reset-schedule")
    public ResponseEntity<List<GeneralClassOpened>> requestResetSchedule(@RequestParam("semester") String semester, @RequestBody ResetScheduleRequest request) {
        log.info("Controler API -> requestResetSchedule start...");

        return ResponseEntity.ok(gService.resetSchedule(request.getIds(), semester));
    }

    @PostMapping("/auto-schedule-time")
    public ResponseEntity<List<GeneralClassOpened>> requestAutoScheduleTime(@RequestParam("semester") String semester, @RequestParam("groupName") String groupName) {
        log.info("Controler API -> requestAutoScheduleTime...");
        return ResponseEntity.ok(gService.autoSchedule(semester, groupName));
    }

    @PostMapping("/auto-schedule-room")
    public ResponseEntity<?> requestAutoScheduleRoom(@RequestParam("semester") String semester, @RequestParam("groupName") String groupName) {
        log.info("Controler API -> requestAutoScheduleRoom...");
        return ResponseEntity.ok(gService.autoScheduleRoom(semester, groupName));
    }
}
