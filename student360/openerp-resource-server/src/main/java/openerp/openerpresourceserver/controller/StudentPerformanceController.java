package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.model.StudentPerformance;
import openerp.openerpresourceserver.service.StudentPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/student-performance")
public class StudentPerformanceController {

    private StudentPerformanceService studentPerformanceService;
    @PreAuthorize("hasRole('ROLE_TEACHER') or (hasRole('ROLE_STUDENT') and #id == principal.name)")
    @GetMapping("/student-performance/{id}")
    public ResponseEntity<?> getPerformanceStudentId(@PathVariable String id, Principal principal) {
        StudentPerformance studentPerformance = studentPerformanceService.getPerformanceStudentId(id);
        return ResponseEntity.ok().body(studentPerformance);
    }
}
