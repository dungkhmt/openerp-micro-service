package openerp.openerpresourceserver.generaltimetabling.controller.general;

import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.exception.*;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.RoomReservationDto;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.*;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.ResetScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.input.ModelInputAutoScheduleTimeSlotRoom;
import openerp.openerpresourceserver.generaltimetabling.model.response.ModelResponseGeneralClass;
import openerp.openerpresourceserver.generaltimetabling.service.ExcelService;
import openerp.openerpresourceserver.generaltimetabling.service.GeneralClassService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;

@RestController
@RequestMapping("/general-classes")
@AllArgsConstructor
@Log4j2
public class GeneralClassController {
    private GeneralClassService gService;
    private ExcelService excelService;
    @ExceptionHandler(ConflictScheduleException.class)
    public ResponseEntity resolveScheduleConflict(ConflictScheduleException e) {
        return ResponseEntity.status(410).body(e.getCustomMessage());
    }

    @ExceptionHandler(InvalidFieldException.class)
    public ResponseEntity resolveInvalidFieldException(InvalidFieldException e) {
        return ResponseEntity.status(420).body(e.getErrorMessage());
    }

    @ExceptionHandler(MinimumTimeSlotPerClassException.class)
    public ResponseEntity resolveMiniumTimeSlotException(MinimumTimeSlotPerClassException e) {
        return ResponseEntity.status(410).body(e.getErrorMessage());
    }

    @ExceptionHandler(InvalidClassStudentQuantityException.class)
    public ResponseEntity resolveScheduleConflict(InvalidClassStudentQuantityException e) {
        return ResponseEntity.status(410).body(e.getCustomMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity resolveNotFoundSolution(NotFoundException e) {
        return ResponseEntity.status(410).body(e.getCustomMessage());
    }

    @ExceptionHandler(ParseException.class)
    public ResponseEntity resolveParseException( ParseException e) {
        return ResponseEntity.status(410).body("Mã lớp hoặc mã lớp tạm thời, mã lớp cha không phải là 1 số!");
    }

    @GetMapping("/")
    public ResponseEntity<List<GeneralClass>> requestGetClasses(@RequestParam("semester") String semester, @RequestParam("groupName") String groupName) {
        try {
            List<GeneralClass> generalClassList = gService.getGeneralClasses(semester, groupName);
            return ResponseEntity.ok(generalClassList);
        } catch(Exception e) {
            System.err.println(e);
            return ResponseEntity.badRequest().body(new ArrayList<>());
        }
    }

    
    @PostMapping("/update-class")
    public ResponseEntity<GeneralClass> requestUpdateClass(@RequestBody UpdateGeneralClassRequest request) {
        GeneralClass updatedGeneralClass= gService.updateGeneralClass(request);
        if(updatedGeneralClass == null) throw new RuntimeException("General Class was null");
        return ResponseEntity.ok().body(updatedGeneralClass);
    }

    @PostMapping("/update-class-schedule")
    public ResponseEntity<GeneralClass> requestUpdateClassSchedule(@RequestParam("semester")String semester, @RequestBody UpdateGeneralClassScheduleRequest request ) {
        GeneralClass updatedGeneralClass= gService.updateGeneralClassSchedule(semester, request);
        if(updatedGeneralClass == null) throw new RuntimeException("General Class was null");
        return ResponseEntity.ok().body(updatedGeneralClass);
    }

    @PostMapping("/update-class-schedule-v2")
    public ResponseEntity<List<GeneralClass>> requestUpdateClassScheduleV2(@RequestParam("semester")String semester, @RequestBody UpdateClassScheduleRequest request ) {
        List<GeneralClass> updatedGeneralClass = gService.v2UpdateClassSchedule(semester, request.getSaveRequests());
        if(updatedGeneralClass.isEmpty()) throw new RuntimeException("General Class was null");
        return ResponseEntity.ok().body(updatedGeneralClass);
    }

    @PostMapping("/update-classes-group")
    public ResponseEntity requestUpdateClassesGroup(@RequestBody UpdateClassesToNewGroupRequest request) {
        try{
            if(request.getPriorityBuilding() == null) return ResponseEntity.ok(gService.addClassesToCreatedGroup(request.getIds(), request.getGroupName()));
            return ResponseEntity.ok(gService.addClassesToNewGroup(request.getIds(),request.getGroupName(),request.getPriorityBuilding()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/get-class-detail-with-subclasses/{classId}")
    public ResponseEntity<?> getClassDetailWithSubClasses(Principal principal, @PathVariable Long classId){
        ModelResponseGeneralClass cls = gService.getClassDetailWithSubClasses(classId);
        return ResponseEntity.ok().body(cls);
    }


    @PostMapping("/export-excel")
    public ResponseEntity requestExportExcel(@RequestParam("semester") String semester) {
        log.info("Controler API -> requestExportExcel start...");
        String filename = String.format("TKB_{}.xlsx", semester);
        InputStreamResource file = new InputStreamResource(excelService.exportGeneralExcel(semester));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/reset-schedule")
    public ResponseEntity<List<GeneralClass>> requestResetSchedule(@RequestParam("semester") String semester, @RequestBody ResetScheduleRequest request) {
        log.info("Controler API -> requestResetSchedule start...");

        return ResponseEntity.ok(gService.resetSchedule(request.getIds(), semester));
    }

    @PostMapping("/auto-schedule-timeslot-room")
    public ResponseEntity<?> autoScheduleTimeSlotRoom(Principal principal, @RequestBody ModelInputAutoScheduleTimeSlotRoom I){
        return ResponseEntity.ok().body(gService.autoScheduleTimeSlotRoom(I.getClassIds(),I.getTimeLimit()));
    }
    @PostMapping("/auto-schedule-time")
    public ResponseEntity<List<GeneralClass>> requestAutoScheduleTime(
            @RequestParam("semester") String semester,
            @RequestParam("groupName") String groupName,
            @RequestParam("timeLimit") int timeLimit) {
        log.info("Controller API -> requestAutoScheduleTime...");
        //return ResponseEntity.ok(gService.autoScheduleGroup(semester, groupName, timeLimit*1000));
        return ResponseEntity.ok(gService.autoSchedule(semester, timeLimit*1000));

    }

    @PostMapping("/auto-schedule-room")
    public ResponseEntity<?> requestAutoScheduleRoom(
            @RequestParam("semester") String semester,
            @RequestParam("groupName") String groupName,
            @RequestParam("timeLimit") int timeLimit) {
        log.info("Controler API -> requestAutoScheduleRoom...");
        return ResponseEntity.ok(gService.autoScheduleRoom(semester, groupName, timeLimit));
    }

    @DeleteMapping("/")
    public ResponseEntity<GeneralClass> requestDeleteClass(@RequestParam("generalClassId") Long generalClassId) {
        return ResponseEntity.ok(gService.deleteClassById(generalClassId));
    }

    @DeleteMapping("/delete-by-ids")
    public ResponseEntity<String> deleteClassesByIds(@RequestBody List<Long> ids) {
        gService.deleteClassesByIds(ids);
        return ResponseEntity.ok("Deleted classes with IDs: " + ids);
    }


    @PostMapping("/{generalClassId}/room-reservations/")
    public ResponseEntity<GeneralClass> requestAddRoomReservation(
            @PathVariable("generalClassId") Long generalClassId,
            @RequestBody RoomReservationDto request) {
        return ResponseEntity.ok(gService.addRoomReservation(generalClassId, request.getParentId(), request.getDuration()));
    }

    @DeleteMapping("/delete-by-semester")
    public ResponseEntity<String> requestDeleteClassesBySemester(@RequestParam("semester")String semester) {
        gService.deleteClassesBySemester(semester);
        return ResponseEntity.ok("Xóa lớp thành công");
    }

    @DeleteMapping("/{generalClassId}/room-reservations/{roomReservationId}")
    public ResponseEntity<String> requestDeleteRoomReservation(
            @PathVariable("generalClassId") Long generalClassId,
            @PathVariable("roomReservationId") Long roomReservationId
    ) {
        gService.deleteRoomReservation(generalClassId, roomReservationId);
        return ResponseEntity.ok("Xóa lớp thành công");
    }
}
