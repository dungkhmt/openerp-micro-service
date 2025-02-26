package openerp.openerpresourceserver.examtimetabling.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamTimetableDTO;
import openerp.openerpresourceserver.examtimetabling.entity.ExamTimetable;
import openerp.openerpresourceserver.examtimetabling.service.ExamTimetableService;

@RestController
@RequestMapping("/exam-timetable")
@RequiredArgsConstructor
public class ExamTimetableController {
    private final ExamTimetableService examTimetableService;
    
    @GetMapping("/plan/{examPlanId}")
    public ResponseEntity<List<ExamTimetableDTO>> getAllTimetablesByExamPlanId(@PathVariable UUID examPlanId) {
        try {
            List<ExamTimetableDTO> timetables = examTimetableService.getAllTimetablesByExamPlanId(examPlanId);
            return ResponseEntity.ok(timetables);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ExamTimetable> createExamTimetable(@Valid @RequestBody ExamTimetable examTimetable) {
        try {
            ExamTimetable createdTimetable = examTimetableService.createExamTimetable(examTimetable);
            return ResponseEntity.ok(createdTimetable);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
