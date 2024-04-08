package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.model.dto.request.academicWeek.CreateAcademicWeekRequest;
import openerp.openerpresourceserver.service.AcademicWeekService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
