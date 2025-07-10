package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.model.StudentStatisticContest;
import openerp.openerpresourceserver.entity.StudentSubmissionStatistics;
import openerp.openerpresourceserver.service.StudentSubmissionStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/student-statistics")
public class StudentSubmissionStatisticsController {

    private StudentSubmissionStatisticsService StudentSubmissionStatisticsService;
    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStudentStatistics() {
        List<StudentSubmissionStatistics> StudentSubmissionDetails = StudentSubmissionStatisticsService.getAllStatisticsDetailStudent();
        return ResponseEntity.ok().body(StudentSubmissionDetails);
    }

    @PreAuthorize("hasRole('ROLE_TEACHER') or (hasRole('ROLE_STUDENT') and #id == principal.name)")
    @GetMapping("/student-contest-statistic/{id}")
    public ResponseEntity<?> getStaticsContestStudentId(@PathVariable String id, Principal principal) {
        StudentStatisticContest studentStatisticContest = StudentSubmissionStatisticsService.getStaticsContestStudentId(id);
        return ResponseEntity.ok().body(studentStatisticContest);
    }
}
