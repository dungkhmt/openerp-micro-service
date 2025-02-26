package openerp.openerpresourceserver.examtimetabling.service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.entity.ExamPlan;
import openerp.openerpresourceserver.examtimetabling.repository.ExamPlanRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ExamPlanService {
  private final ExamPlanRepository examPlanRepository;

  public List<ExamPlan> getAllExamPlans() {
    return examPlanRepository.findAll();
  }

  public ExamPlan createExamPlan(ExamPlan examPlan) {
    examPlan.setId(UUID.randomUUID());

    return examPlanRepository.save(examPlan);
  }

  @Transactional
  public ExamPlan updateExamPlan(UUID id, ExamPlan examPlanDetails) {
    ExamPlan examPlan = examPlanRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Exam plan not found with id: " + id));

    examPlan.setName(examPlanDetails.getName());
    examPlan.setDescription(examPlanDetails.getDescription());
    examPlan.setStartTime(examPlanDetails.getStartTime());
    examPlan.setEndTime(examPlanDetails.getEndTime());

    return examPlanRepository.save(examPlan);
  }

  @Transactional
  public void softDeleteExamPlan(UUID id) {
    ExamPlan examPlan = examPlanRepository.findById(id)
        .orElseThrow();

    examPlan.setDeleteAt(LocalDateTime.now());
    examPlanRepository.save(examPlan);
  }

  public List<ExamPlan> findAllActivePlans() {
    return examPlanRepository.findByDeleteAtIsNull();
  }

  public ExamPlan getExamPlanById(UUID id) {
    return examPlanRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Exam plan not found with id: " + id));
}
}
