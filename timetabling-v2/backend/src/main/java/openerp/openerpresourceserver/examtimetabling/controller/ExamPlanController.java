package openerp.openerpresourceserver.examtimetabling.controller;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamPlanStatisticsDTO;
import openerp.openerpresourceserver.examtimetabling.entity.ExamPlan;
import openerp.openerpresourceserver.examtimetabling.service.ExamPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exam-plan")
@RequiredArgsConstructor
public class ExamPlanController {
    private final ExamPlanService examPlanService;

    @GetMapping("/{id}")
    public ResponseEntity<ExamPlan> getExamPlanById(@PathVariable UUID id) {
        try {
            ExamPlan examPlan = examPlanService.getExamPlanById(id);
            return ResponseEntity.ok(examPlan);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ExamPlan> updateExamPlan(
            @Valid @RequestBody ExamPlan examPlanDetails) {
        try {
            ExamPlan updatedPlan = examPlanService.updateExamPlan(examPlanDetails.getId(), examPlanDetails);
            return ResponseEntity.ok(updatedPlan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<?> softDeleteExamPlan(@PathVariable UUID id) {
        try {
            examPlanService.softDeleteExamPlan(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ExamPlan>> getAllExamPlans() {
        List<ExamPlan> examPlans = examPlanService.findAllActivePlans();
        return ResponseEntity.ok(examPlans);
    }

    @PostMapping("/create")
    public ResponseEntity<ExamPlan> createExamPlan(@Valid @RequestBody ExamPlan examPlan) {
        try {
            System.err.println("Creating exam plan" + examPlan);
            ExamPlan createdPlan = examPlanService.createExamPlan(examPlan);
            return ResponseEntity.ok(createdPlan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/statistics")
    public ResponseEntity<ExamPlanStatisticsDTO> getExamPlanStatistics(@PathVariable UUID id) {
        try {
            ExamPlanStatisticsDTO statistics = examPlanService.getExamPlanStatistics(id);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
