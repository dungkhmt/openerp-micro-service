package openerp.openerpresourceserver.generaltimetabling.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.academicWeek.CreateAcademicWeekRequest;
import openerp.openerpresourceserver.generaltimetabling.service.AcademicWeekService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/academic-weeks")
@AllArgsConstructor
public class AcademicWeekController {
    private AcademicWeekService academicWeekService;
    @PostMapping("/")
    public ResponseEntity makeAcademicWeek(@RequestBody CreateAcademicWeekRequest request) {
        return ResponseEntity.ok(academicWeekService.saveAcademicWeeks(request.getSemester(), request.getStartDate(), request.getNumberOfWeeks()));
    }
    @GetMapping("/")
    public ResponseEntity getAllAcademicWeek(@RequestParam String semester) {
        return ResponseEntity.ok(academicWeekService.getAllWeeks(semester));
    }
    @DeleteMapping("/")
    public ResponseEntity deleteAllBySemester(@RequestParam String semester) {
        academicWeekService.deleteAllBySemester(semester);
        return ResponseEntity.ok("ok");
    }
    
}
