package openerp.openerpresourceserver.controller;

import jakarta.validation.Valid;
import openerp.openerpresourceserver.model.dto.request.SemesterDto;
import openerp.openerpresourceserver.model.entity.Semester;
import openerp.openerpresourceserver.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/update")
    public ResponseEntity<Void> updateSemester() {
        try {
            service.updateSemester();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Semester> createNewSemester(@Valid @RequestBody SemesterDto semesterDto) {
        try {
            Semester semester = service.create(semesterDto);
            return new ResponseEntity<>(semester, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}