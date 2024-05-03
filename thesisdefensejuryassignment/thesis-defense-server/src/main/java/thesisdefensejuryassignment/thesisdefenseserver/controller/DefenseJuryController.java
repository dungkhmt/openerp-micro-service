package thesisdefensejuryassignment.thesisdefenseserver.controller;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseJury;
import thesisdefensejuryassignment.thesisdefenseserver.entity.Teacher;
import thesisdefensejuryassignment.thesisdefenseserver.entity.Thesis;
import thesisdefensejuryassignment.thesisdefenseserver.models.AssignReviewerToThesisIM;
import thesisdefensejuryassignment.thesisdefenseserver.models.AssignTeacherAndThesisToDefenseJuryIM;
import thesisdefensejuryassignment.thesisdefenseserver.models.AssignTeacherToDefenseJuryAutomaticallyIM;
import thesisdefensejuryassignment.thesisdefenseserver.models.DefenseJuryIM;
import thesisdefensejuryassignment.thesisdefenseserver.service.DefenseJuryServiceImpl;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/defense-jury")
@Log4j2

public class DefenseJuryController {
    private DefenseJuryServiceImpl juryService;


    private static Logger logger = LogManager.getLogger(DefenseJuryController.class);

    @PostMapping("/save")
    public ResponseEntity<DefenseJury> createDefenseJury(
            @RequestBody DefenseJuryIM request
    ){
        DefenseJury createdJury = juryService.createNewDefenseJury(request);
        if (createdJury == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(createdJury, HttpStatus.OK);
    }

    @GetMapping("/teachers")

    public ResponseEntity<List<Teacher>> getAllTeachers() {
        List<Teacher> res = juryService.getAllTeachers();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefenseJury> getDefenseJuryById(@PathVariable String id) {
        System.out.println(id);
        DefenseJury res = juryService.getDefenseJuryByID(UUID.fromString(id));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @GetMapping("/thesis/get-all-available/{thesisDefensePlanId}")
    public ResponseEntity<List<Thesis>> getAllAvailableThesis(@PathVariable String thesisDefensePlanId) {
        return new ResponseEntity<>(juryService.getAllAvailableThesiss(thesisDefensePlanId), HttpStatus.OK);
    }

    @PostMapping("/assign")
    public ResponseEntity<DefenseJury> assignTeacherAndThesisToDefenseJury(
            @RequestBody AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList
    ) {
        DefenseJury defenseJury = juryService.assignTeacherAndThesis(teacherAndThesisList);
        if (defenseJury == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(defenseJury, HttpStatus.CREATED);
    }

    @PostMapping("/assign-defense-teacher")
    public ResponseEntity<DefenseJury> assignReviewerToThesis(
            @RequestBody AssignReviewerToThesisIM teacherAndThesisList
    ) {
        DefenseJury defenseJury = juryService.assignReviewerToThesis(teacherAndThesisList);
        if (defenseJury == null){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(defenseJury, HttpStatus.CREATED);
    }

    @PostMapping("/assign-automatically")
    public ResponseEntity<String> assignTeacherAndThesisAutomatically(
            @RequestBody AssignTeacherToDefenseJuryAutomaticallyIM teacherListAndDefensePlan
    ) {
        String message = juryService.assignTeacherAndThesisAutomatically(teacherListAndDefensePlan);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

}
