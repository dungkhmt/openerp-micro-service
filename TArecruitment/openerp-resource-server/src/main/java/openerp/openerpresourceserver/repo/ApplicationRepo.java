package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {
    List<Application> findByUserId(String userId);
    List<Application> findByClassCallId(int classCallId);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester AND a.applicationStatus = :status")
    List<Application> findByApplicationStatusAndSemester(String status, String semester);

    @Query("SELECT a FROM Application a WHERE a.id IN " +
            "(SELECT MAX(a2.id) FROM Application a2 GROUP BY a2.user.id)")
    List<Application> findDistinctApplicationsByUser();

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester")
    List<Application> findApplicationsByClassSemester(String semester);

    @Query("SELECT a from Application a WHERE a.user.id = :id AND a.assignStatus = :status AND a.classCall.semester = :semester")
    List<Application> findApplicationByUserIdAndAssignStatus(String id, String status, String semester);
}
