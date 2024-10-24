package thesisdefensejuryassignment.thesisdefenseserver.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thesisdefensejuryassignment.thesisdefenseserver.dto.ThesisDTO;
import thesisdefensejuryassignment.thesisdefenseserver.entity.DefenseJury;
import thesisdefensejuryassignment.thesisdefenseserver.entity.Thesis;
import thesisdefensejuryassignment.thesisdefenseserver.entity.TrainingProgram;
import thesisdefensejuryassignment.thesisdefenseserver.models.ThesisModel;
import thesisdefensejuryassignment.thesisdefenseserver.service.ThesisServiceImpl;

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
}
