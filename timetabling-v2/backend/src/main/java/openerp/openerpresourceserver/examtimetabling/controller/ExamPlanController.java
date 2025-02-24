package openerp.openerpresourceserver.examtimetabling.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.entity.ExamPlan;
import openerp.openerpresourceserver.examtimetabling.service.ExamPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/exam-plan")
@RequiredArgsConstructor
public class ExamPlanController {
    private final ExamPlanService examPlanService;
    
    @GetMapping
    public ResponseEntity<List<ExamPlan>> getAllExamPlans() {
        List<ExamPlan> examPlans = examPlanService.getAllExamPlans();
        return ResponseEntity.ok(examPlans);
    }

    @PostMapping("/create")
    public ResponseEntity<ExamPlan> createExamPlan(@Valid @RequestBody ExamPlan examPlan) {
        try {
            ExamPlan createdPlan = examPlanService.createExamPlan(examPlan);
            return ResponseEntity.ok(createdPlan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
