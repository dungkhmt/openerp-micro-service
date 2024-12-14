package openerp.openerpresourceserver.thesisdefensejuryassignment.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.DefenseJuryDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDefensePlanDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.ThesisDefensePlan;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisDefensePlanIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.UpdateThesisDefensePlanIM;
import openerp.openerpresourceserver.thesisdefensejuryassignment.service.ThesisDefensePlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/thesis-defense-plan")
public class ThesisDefensePlanController {
    private ThesisDefensePlanService graduationTermService;

    @GetMapping("/get-all")
    public ResponseEntity<List<ThesisDefensePlanDTO>> getAllGraduationTerm (){
        try{
            return new ResponseEntity<>(graduationTermService.getAllThesisDefensePlan(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThesisDefensePlan> getThesisDefensePlanById (@PathVariable String id){
        try{
            return new ResponseEntity<>(graduationTermService.getThesisDefensePlanById(id), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get-assigned-for-teacher/{teacherId}")
    public ResponseEntity<List<ThesisDefensePlan>> getAllThesisDefensePlanAssignedForTeacher (@PathVariable String teacherId){
        try{
            return new ResponseEntity<>(graduationTermService.getAllThesisDefensePlanAssignedForTeacherWithId(teacherId), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-assigned-for-teacher/{teacherId}/{thesisDefensePlanId}")
    public ResponseEntity<List<DefenseJuryDTO>> getThesisDefensePlanAssignedForTeacherById (@PathVariable String teacherId, @PathVariable String thesisDefensePlanId ){
        System.out.println("Teacher "+ teacherId + "plan " + thesisDefensePlanId);
        try{
            return new ResponseEntity<>(graduationTermService.getThesisDefensePlanAssignedForTeacherWithTeacherId(teacherId, thesisDefensePlanId), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get-assigned-for-teacher/{teacherId}/president")
    public ResponseEntity<List<ThesisDefensePlan>> getAllThesisDefensePlanAssignedForTeacherAsPresident (@PathVariable String teacherId){
        try{
            return new ResponseEntity<>(graduationTermService.getAllThesisDefensePlanAssignedForTeacherAsPresidentWithId(teacherId), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/get-assigned-for-teacher/{teacherId}/{thesisDefensePlanId}/president")
    public ResponseEntity<List<DefenseJuryDTO>> getThesisDefensePlanAssignedForTeacherByIdAndRolePresident (@PathVariable String teacherId, @PathVariable String thesisDefensePlanId ){
        System.out.println("Teacher "+ teacherId + "plan " + thesisDefensePlanId);
        try{
            return new ResponseEntity<>(graduationTermService.getThesisDefensePlanWithTeacherRoleAsPresidentAndTeacherIdById(teacherId, thesisDefensePlanId), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/save")
    public  ResponseEntity<String> saveGraduationTerm(@RequestBody ThesisDefensePlanIM graduationTerm){
        ThesisDefensePlan savedGraduationTerm = graduationTermService.createThesisDefensePlan(graduationTerm);
        if (savedGraduationTerm != null){
            return new ResponseEntity<>("Create successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Create failed", HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/edit")
    public  ResponseEntity<String> updateGraduationTerm(@RequestParam String id, @RequestBody UpdateThesisDefensePlanIM graduationTerm){
        System.out.println(id);
        ThesisDefensePlan savedGraduationTerm = graduationTermService.updateThesisDefensePlan(id, graduationTerm);
        if (savedGraduationTerm != null){
            return new ResponseEntity<>("Update successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Update failed", HttpStatus.BAD_REQUEST);
    }
}
