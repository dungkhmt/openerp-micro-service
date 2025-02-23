package openerp.openerpresourceserver.examtimetabling.repository;

import openerp.openerpresourceserver.examtimetabling.entity.ExamClass;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ExamClassRepository extends JpaRepository<ExamClass, String> {
    @Modifying
    @Query("DELETE FROM ExamClass e WHERE e.examClassId IN :ids")
    void deleteByExamClassIdIn(List<String> ids);

    @Query("SELECT e.examClassId FROM ExamClass e WHERE e.examClassId IN :ids")
    List<String> findExamClassIdsByExamClassIdIn(Set<String> ids);
}
