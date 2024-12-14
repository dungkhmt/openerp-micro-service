package openerp.openerpresourceserver.tarecruitment.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.tarecruitment.service.TARecruitment_SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/ta-semester")
public class TARecruitment_SemesterController {

    private TARecruitment_SemesterService semesterService;
    @GetMapping("/get-all-semester")
    public ResponseEntity<?> getAllSemester() {
        List<String> semesters = semesterService.getAllSemester();
        return ResponseEntity.ok().body(semesters);
    }

    @GetMapping("/get-current-semester")
    public ResponseEntity<?> getCurrentSemester() {
        String semester = semesterService.getCurrentSemester();
        return ResponseEntity.ok().body(semester);
    }
}
