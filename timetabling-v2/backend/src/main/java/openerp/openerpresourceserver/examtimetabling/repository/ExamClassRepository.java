package openerp.openerpresourceserver.examtimetabling.repository;

import openerp.openerpresourceserver.examtimetabling.entity.ExamClass;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ExamClassRepository extends JpaRepository<ExamClass, UUID> {
    long countByExamPlanId(UUID examPlanId);
    
    @Query("SELECT e.school as school, COUNT(e) as count FROM ExamClass e " +
           "WHERE e.examPlanId = :examPlanId GROUP BY e.school ORDER BY COUNT(e) DESC")
    List<Object[]> getSchoolDistributionByExamPlanId(UUID examPlanId);
    
    @Modifying
    @Query("DELETE FROM ExamClass e WHERE e.id IN :ids")
    void deleteByExamClassIdIn(List<UUID> ids);

    @Query("SELECT e.examClassId FROM ExamClass e WHERE e.examClassId IN :ids")
    List<String> findExamClassIdsByExamClassIdIn(Set<String> ids);

    List<ExamClass> findByExamPlanId(UUID examPlanId);

    @Query("SELECT e.examClassId FROM ExamClass e WHERE e.examPlanId = :examPlanId")
    List<String> findExamClassIdsByExamPlanId(UUID examPlanId);

    boolean existsByExamClassIdAndExamPlanId(String examClassId, UUID examPlanId);

    @Query("SELECT e FROM ExamClass e WHERE e.examPlanId = :examPlanId AND e.examClassId IN :examClassIds")
    List<ExamClass> findByExamPlanIdAndExamClassIdIn(UUID examPlanId, List<String> examClassIds);
}
