package openerp.openerpresourceserver.thesisdefensejuryassignment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.TeacherSupervisedThesisDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.dto.ThesisDTO;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.AssignJuryTopicToThesisIM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.DefenseJury;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.Thesis;
import openerp.openerpresourceserver.thesisdefensejuryassignment.entity.TrainingProgram;
import openerp.openerpresourceserver.thesisdefensejuryassignment.models.ThesisModel;
import openerp.openerpresourceserver.thesisdefensejuryassignment.service.ThesisServiceImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/thesis")
@Log4j2

public class ThesisController {
    @Autowired
    private ThesisServiceImpl thesisService;

    @PostMapping("/save")
    public ResponseEntity<Thesis> createNewThesis(@RequestBody ThesisModel thesisModel) {
        System.out.println("thesisplan: " + thesisModel.getThesisDefensePlanId());
        Thesis thesis = thesisService.createNewThesis(thesisModel);
        if (thesis == null) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(thesis, HttpStatus.CREATED);
    }

    @GetMapping("/training-program")
    public ResponseEntity<List<TrainingProgram>> getAllTrainingProgram() {
        return new ResponseEntity<>(thesisService.getAllTrainingProgram(), HttpStatus.OK);
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ThesisDTO>> getAllThesisByStudentEmail(@RequestParam(name = "student-email") String studentEmail) {
        List<ThesisDTO> thesisList = thesisService.getAllByStudentEmail(studentEmail);
        if (thesisList == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(thesisList, HttpStatus.OK);
    }

    @GetMapping("/get-all-by-supervisor")
    public ResponseEntity<List<TeacherSupervisedThesisDTO>> getAllThesisSupervisedByTeacher(@RequestParam(name = "teacher") String teacherId) {
        System.out.println(teacherId);
        List<TeacherSupervisedThesisDTO> thesisList = thesisService.getAllThesisSupervisedByTeacher(teacherId);
        if (thesisList == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(thesisList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Thesis> getThesisById(@PathVariable String id) {
        Thesis thesisList = thesisService.getById(id);
        return new ResponseEntity<>(thesisList, HttpStatus.OK);
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignJuryTopicToThesis(@RequestBody AssignJuryTopicToThesisIM assignJuryTopicToThesisIM) {
        String message = thesisService.assignJuryTopicToThesis(assignJuryTopicToThesisIM.getThesisId(), assignJuryTopicToThesisIM);
        if (message.equals("ERROR")) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

}
