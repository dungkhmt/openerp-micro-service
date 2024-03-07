package thesisdefensejuryassignment.thesisdefenseserver.controller;

import lombok.AllArgsConstructor;
import thesisdefensejuryassignment.thesisdefenseserver.entity.ThesisDefensePlan;
import thesisdefensejuryassignment.thesisdefenseserver.service.ThesisDefensePlanService;
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
    public ResponseEntity<List<ThesisDefensePlan>> getAllGraduationTerm (){
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
    @PostMapping("/save")
    public  ResponseEntity<String> saveGraduationTerm(@RequestBody ThesisDefensePlan graduationTerm){
        ThesisDefensePlan savedGraduationTerm = graduationTermService.createThesisDefensePlan(graduationTerm);
        if (savedGraduationTerm != null){
            return new ResponseEntity<>("Create successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Create failed", HttpStatus.BAD_REQUEST);
    }
}
