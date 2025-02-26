package openerp.openerpresourceserver.examtimetabling.service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.entity.ExamPlan;
import openerp.openerpresourceserver.examtimetabling.repository.ExamPlanRepository;
import org.springframework.stereotype.Service;

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
}
