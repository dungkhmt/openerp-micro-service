package openerp.openerpresourceserver.controller.general;

import java.util.ArrayList;
import java.util.List;

import openerp.openerpresourceserver.model.dto.request.general.UpdateClassesToNewGroupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassRequest;
import openerp.openerpresourceserver.model.dto.request.general.UpdateGeneralClassScheduleRequest;
import openerp.openerpresourceserver.model.entity.general.GeneralClassOpened;
import openerp.openerpresourceserver.service.GeneralClassOpenedService;

@RestController
@RequestMapping("/general-classes")
public class GeneralClassOpenedController {
    @Autowired
    private GeneralClassOpenedService gService;


    @GetMapping("/")
    public ResponseEntity<List<GeneralClassOpened>> getClasses(@RequestParam String semester) {
        try {
            List<GeneralClassOpened> generalClassOpenedList = gService.getGeneralClasses(semester);
            System.out.println(generalClassOpenedList);
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
    public ResponseEntity<GeneralClassOpened> updateClassSchedule(@RequestBody UpdateGeneralClassScheduleRequest request ) {
        GeneralClassOpened updatedGeneralClass= gService.updateGeneralClassSchedule(request);
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
}
