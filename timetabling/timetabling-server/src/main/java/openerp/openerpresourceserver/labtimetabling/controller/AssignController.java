package openerp.openerpresourceserver.labtimetabling.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.labtimetabling.entity.Assign;
import openerp.openerpresourceserver.labtimetabling.entity.ScheduleConflict;
import openerp.openerpresourceserver.labtimetabling.entity.dto.AssignDTO;
import openerp.openerpresourceserver.labtimetabling.entity.dto.AssignDTOUpdateRequest;
import openerp.openerpresourceserver.labtimetabling.entity.dto.AssignResponse;
import openerp.openerpresourceserver.labtimetabling.entity.dto.ScheduleConflictResponse;
import openerp.openerpresourceserver.labtimetabling.repo.AssignRepo;
import openerp.openerpresourceserver.labtimetabling.service.AssignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/lab-timetabling/assign")
public class AssignController {
    private AssignRepo assignRepo;
    private AssignService assignService;

    @PostMapping
    public ResponseEntity<?> createAssign(@RequestBody AssignDTOUpdateRequest request){
        System.out.println(request.getUpdateSet().stream().map(AssignDTO::toString).toList());
        System.out.println(request.getRemoveSet().stream().map(AssignDTO::toString).toList());
        List<Assign> assignList = request.getUpdateSet().stream().map(Assign::new).toList();
        assignRepo.saveAll(assignList);

        if (!request.getRemoveSet().isEmpty())
            assignRepo.deleteAllById(request.getRemoveSet().stream().map(AssignDTO::getId).toList());

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @GetMapping(value ="/get-all")
    public ResponseEntity<?> getAll(){
        List<String> semesters = assignService.getAllSemesters();
        Map<String, Object> body = new HashMap<>();
        if(!semesters.isEmpty()) {
            Collections.sort(semesters);
            List<Assign> assigns = assignService.getAssignsBySemester(Long.valueOf(semesters.get(semesters.size() - 1)));
            List<AssignResponse> assignResponseList = assigns.stream().map(AssignResponse::new).toList();
            body.put("assigns", assignResponseList);
            body.put("semesters", semesters);
        }
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }
    @PostMapping("/check-conflict")
    public ResponseEntity<?> conflictChecking(){
        List<ScheduleConflict> scheduleConflicts = assignService.findConflict();
        List<ScheduleConflictResponse> scheduleConflictResponseList = scheduleConflicts.stream().map(ScheduleConflictResponse::new).toList();
        return ResponseEntity.status(HttpStatus.OK).body(scheduleConflictResponseList);
    }
    @GetMapping("/semester/{semester}")
    public ResponseEntity<?> getClassesBySemester(@PathVariable Long semester){
        List<Assign> assigns = assignService.getAssignsBySemester(semester);
        List<AssignResponse> responses = assigns.stream().map(AssignResponse::new).toList();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
