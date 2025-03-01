package openerp.openerpresourceserver.examtimetabling.service;

import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamPlanStatisticsDTO;
import openerp.openerpresourceserver.examtimetabling.dtos.ExamPlanStatisticsDTO.SchoolStatistic;
import openerp.openerpresourceserver.examtimetabling.entity.ExamPlan;
import openerp.openerpresourceserver.examtimetabling.repository.ExamClassRepository;
import openerp.openerpresourceserver.examtimetabling.repository.ExamPlanRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamPlanService {
  private final ExamPlanRepository examPlanRepository;
  private final ExamClassRepository examClassRepository;

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

  public ExamPlanStatisticsDTO getExamPlanStatistics(UUID examPlanId) {
    // Verify the exam plan exists
    ExamPlan examPlan = examPlanRepository.findById(examPlanId)
        .orElseThrow(() -> new RuntimeException("Exam plan not found with id: " + examPlanId));
    
    // Get total count of classes for this plan
    long totalCount = examClassRepository.countByExamPlanId(examPlanId);
    
    // Get school distribution
    List<Object[]> schoolCounts = examClassRepository.getSchoolDistributionByExamPlanId(examPlanId);
    
    // Build statistics DTO
    ExamPlanStatisticsDTO stats = new ExamPlanStatisticsDTO();
    stats.setExamPlanId(examPlanId);
    stats.setExamPlanName(examPlan.getName());
    stats.setTotalExamClasses(totalCount);
    
    // Process top schools
    List<SchoolStatistic> allSchools = processSchoolCounts(schoolCounts, totalCount);
    
    // Get top 4 and calculate others
    List<SchoolStatistic> topSchools = new ArrayList<>();
    SchoolStatistic otherSchools = new SchoolStatistic();
    otherSchools.setSchoolName("Others");
    otherSchools.setCount(0);
    otherSchools.setPercentage(0.0);
    
    if (allSchools.size() <= 6) {
      // Less than or equal to 6 schools, no "Others" category needed
      stats.setTopSchools(allSchools);
    } else {
        // More than 6 schools, create "Others" category
        topSchools = allSchools.subList(0, 6);
        
        // Calculate "Others" total
        long othersCount = 0;
        for (int i = 6; i < allSchools.size(); i++) {
            othersCount += allSchools.get(i).getCount();
        }
        otherSchools.setCount(othersCount);
        otherSchools.setPercentage((double) othersCount * 100 / totalCount);
        
        stats.setTopSchools(topSchools);
        stats.setOtherSchools(otherSchools);
    }
    
    return stats;
  }

  private List<SchoolStatistic> processSchoolCounts(List<Object[]> schoolCounts, long totalCount) {
      return schoolCounts.stream()
          .map(row -> {
              SchoolStatistic stat = new SchoolStatistic();
              stat.setSchoolName((String) row[0]);
              stat.setCount(((Number) row[1]).longValue());
              stat.setPercentage(((double) stat.getCount() * 100) / totalCount);
              return stat;
          })
          .sorted((s1, s2) -> Long.compare(s2.getCount(), s1.getCount())) // Sort by count desc
          .collect(Collectors.toList());
  }
}
