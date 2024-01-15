package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.exception.SemesterNotFoundException;
import openerp.openerpresourceserver.exception.SemesterUsedException;
import openerp.openerpresourceserver.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.model.entity.Semester;
import openerp.openerpresourceserver.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/semester")
public class SemesterController {

    @Autowired
    private SemesterService service;

    @GetMapping("/get-all")
    public ResponseEntity<List<Semester>> getAllSemester() {
        try {
            List<Semester> semesterList = service.getSemester();
            if (semesterList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(semesterList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateSemester(@Valid @RequestBody SemesterDto requestDto) {
        try {
            service.updateSemester(requestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createNewSemester(@Valid @RequestBody SemesterDto semesterDto) {
        try {
            Semester semester = service.create(semesterDto);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } catch (SemesterUsedException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteById(@RequestParam Long id) {
        try {
            service.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (SemesterNotFoundException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (SemesterUsedException e) {
            return new ResponseEntity<>(e.getCustomMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
