package openerp.openerpresourceserver.thesisdefensejuryassignment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.DefenseJuryDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Teacher;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Thesis;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.*;
import openerp.openerpresourceserver.thesisdefensejuryassignment.service.DefenseJuryServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/defense-jury")
@Log4j2

public class DefenseJuryController {
    @Autowired
    private DefenseJuryServiceImpl juryService;


    private static Logger logger = LogManager.getLogger(DefenseJuryController.class);

    @PostMapping("/save")
    public ResponseEntity<String> createDefenseJury(
            @RequestBody DefenseJuryIM request
    ) {
        String createdJury = juryService.createNewDefenseJury(request);
        if (createdJury == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(createdJury, HttpStatus.OK);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateDefenseJury(
            @RequestBody UpdateDefenseJuryIM request
    ) {
        System.out.println("session: " + request.getDefenseSessionId());
        String updatedDefenseJury = juryService.updateDefenseJury(request);
        if (updatedDefenseJury == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(updatedDefenseJury, HttpStatus.CREATED);
    }

    @GetMapping("/teachers")

    public ResponseEntity<List<Teacher>> getAllTeachers() {
        List<Teacher> res = juryService.getAllTeachers();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DefenseJuryDTO> getDefenseJuryById(@PathVariable String id) {
        logger.info("Jury id: " + id);
        System.out.println(id);
        DefenseJuryDTO res = juryService.getDefenseJuryByID(UUID.fromString(id));
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDefenseJuryById(@PathVariable String id) {
        logger.info("Deleted Jury id: " + id);
        DefenseJury res = juryService.deleteDefenseJuryByID(UUID.fromString(id));
        if (res == null) {
            return new ResponseEntity<>("Xóa hội đồng không thành công", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Xóa hội đồng thành công", HttpStatus.OK);
    }


    @GetMapping("/thesis/get-all-available/{thesisDefensePlanId}")
    public ResponseEntity<List<ThesisDTO>> getAllAvailableThesis(@PathVariable String thesisDefensePlanId) {
        System.out.println("Get available thesis: " + thesisDefensePlanId);
        return new ResponseEntity<>(juryService.getAllAvailableThesiss(thesisDefensePlanId), HttpStatus.OK);
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignTeacherAndThesisToDefenseJury(
            @RequestBody AssignTeacherAndThesisToDefenseJuryIM teacherAndThesisList
    ) {
        String message = juryService.assignTeacherAndThesis(teacherAndThesisList);
        if (message == null) {
            return new ResponseEntity<>("Không thể phân công hội đồng", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (message.contains("Giáo viên")) {
            return new ResponseEntity<>(message, HttpStatus.OK);
        }
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }


    @PostMapping("/assign-defense-teacher")
    public ResponseEntity<DefenseJury> assignReviewerToThesis(
            @RequestBody AssignReviewerToThesisIM teacherAndThesisList
    ) {
        DefenseJury defenseJury = juryService.assignReviewerToThesis(teacherAndThesisList);
        if (defenseJury == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(defenseJury, HttpStatus.CREATED);
    }

    @PostMapping("/assign-automatically/{thesisDefensePlanId}/{defenseJuryId}")
    public ResponseEntity<List<Teacher>> assignTeacherAutomatically(
            @PathVariable String thesisDefensePlanId, @PathVariable String defenseJuryId, @RequestBody AssignTeacherToDefenseJuryAutomaticallyIM thesis
    ) {
        List<Teacher> teacherIdList = juryService.assignTeacherAutomatically(thesisDefensePlanId, defenseJuryId, thesis);
        return new ResponseEntity<>(teacherIdList, HttpStatus.CREATED);
    }

    @PostMapping("/reassign")
    public ResponseEntity<DefenseJury> reassignTeacherAndThesis(
            @RequestBody AssignTeacherAndThesisToDefenseJuryIM teacherListAndThesis
    ) {
        DefenseJury defenseJury = juryService.reassignTeacherAndThesis(teacherListAndThesis);
        if (defenseJury == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(defenseJury, HttpStatus.CREATED);
    }

    @GetMapping("/thesis/get-all-available/{thesisDefensePlanId}/{defenseJuryId}")
    public ResponseEntity<List<Thesis>> getAllAvailableThesis(@PathVariable String thesisDefensePlanId, @PathVariable String defenseJuryId) {
        return new ResponseEntity<>(juryService.getAvailableThesisByJuryTopic(thesisDefensePlanId, defenseJuryId), HttpStatus.OK);
    }
}
