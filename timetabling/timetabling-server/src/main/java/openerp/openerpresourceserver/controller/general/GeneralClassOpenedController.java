package openerp.openerpresourceserver.controller.general;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.exception.ConflictScheduleException;
import openerp.openerpresourceserver.model.dto.request.general.UpdateClassesToNewGroupRequest;
import openerp.openerpresourceserver.model.entity.general.ResetScheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.service.GeneralClassOpenedService;

@RestController
@RequestMapping("/general-classes")
@AllArgsConstructor
public class GeneralClassOpenedController {
    private GeneralClassOpenedService gService;
    @ExceptionHandler(ConflictScheduleException.class)
    public ResponseEntity scheduleConflict(ConflictScheduleException e) {
        return ResponseEntity.status(400).body(e.getCustomMessage());
    }

    @GetMapping("/")
    public ResponseEntity<List<GeneralClassOpened>> getClasses(@RequestParam String semester) {
        try {
            List<GeneralClassOpened> generalClassOpenedList = gService.getGeneralClasses(semester);
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
        return ResponseEntity.ok(gService.resetSchedule(request.getIds(), semester));
    }

    @PostMapping("/auto-schedule-time")
    public ResponseEntity<List<GeneralClassOpened>> requestAutoScheduleTime(@RequestParam("semester") String semester, @RequestParam("groupName") String groupName) {
        return ResponseEntity.ok(gService.autoSchedule(semester, groupName));
    }

}
