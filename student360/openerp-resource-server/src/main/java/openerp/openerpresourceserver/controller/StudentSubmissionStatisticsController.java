package openerp.openerpresourceserver.controller;

import lombok.AllArgsConstructor;
import openerp.openerpresourceserver.model.StudentPerformance;
import openerp.openerpresourceserver.model.StudentStatisticContest;
import openerp.openerpresourceserver.model.StudentSubmissionDetail;
import openerp.openerpresourceserver.service.StudentSubmissionStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
@RequestMapping("/student-statistics")
public class StudentSubmissionStatisticsController {

    private StudentSubmissionStatisticsService StudentSubmissionStatisticsService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllStudentStatistics() {
        List<StudentSubmissionDetail> StudentSubmissionDetails = StudentSubmissionStatisticsService.getAllStatisticsDetailStudent();
        return ResponseEntity.ok().body(StudentSubmissionDetails);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getStatisticsDetailStudentId(@PathVariable String id) {
        StudentSubmissionDetail studentSubmissionDetail = StudentSubmissionStatisticsService.getStatisticsDetailStudentId(id);
        return ResponseEntity.ok().body(studentSubmissionDetail);
    }

    @GetMapping("/student-contest-statistic/{id}")
    public ResponseEntity<?> getStaticsContestStudentId(@PathVariable String id) {
        StudentStatisticContest studentStatisticContest = StudentSubmissionStatisticsService.getStaticsContestStudentId(id);
        return ResponseEntity.ok().body(studentStatisticContest);
    }

    @GetMapping("/student-performance/{id}")
    public ResponseEntity<?> getPerformanceStudentId(@PathVariable String id) {
        StudentPerformance studentPerformance = StudentSubmissionStatisticsService.getPerformanceStudentId(id);
        return ResponseEntity.ok().body(studentPerformance);
    }
}
