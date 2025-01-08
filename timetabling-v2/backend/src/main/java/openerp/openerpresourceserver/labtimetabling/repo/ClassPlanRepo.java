package openerp.openerpresourceserver.labtimetabling.repo;

import openerp.openerpresourceserver.labtimetabling.entity.ClassPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassPlanRepo extends JpaRepository<ClassPlan, Long> {
    @Query(value="SELECT DISTINCT semester FROM \"opening-class-plan\"", nativeQuery = true)
    List<String> findDistinctSemesters();
    List<ClassPlan> findAllBySemester(String sem);
}
