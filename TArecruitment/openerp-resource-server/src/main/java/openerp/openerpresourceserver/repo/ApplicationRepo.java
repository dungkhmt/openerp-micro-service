package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {

    @Query("SELECT a FROM Application a WHERE a.user.id = :userId ORDER BY a.classCall.id ASC")
    List<Application> findByUserId(String userId);

    @Query("SELECT a FROM Application a WHERE a.classCall.id = :classCallId ORDER BY a.classCall.id ASC")
    List<Application> findByClassCallId(int classCallId);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester AND a.applicationStatus = :status ORDER BY a.classCall.id ASC")
    List<Application> findByApplicationStatusAndSemester(String status, String semester);

    @Query("SELECT a FROM Application a WHERE a.id IN " +
            "(SELECT MAX(a2.id) FROM Application a2 GROUP BY a2.user.id) ORDER BY a.classCall.id ASC")
    List<Application> findDistinctApplicationsByUser();

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester ORDER BY a.classCall.id ASC")
    List<Application> findApplicationsByClassSemester(String semester);

    @Query("SELECT a FROm Application a WHERE a.user.id = :id AND a.assignStatus = :status " +
            "AND a.classCall.semester = :semester ORDER BY a.classCall.id ASC")
    List<Application> findApplicationByUserIdAndAssignStatus(String id, String status, String semester);

    @Query("SELECT DISTINCT a.user.id FROM Application a WHERE a.classCall.semester = :semester")
    List<String> findDistinctUserIdsBySemester(String semester);

    @Query("SELECT DISTINCT a.classCall.id FROM Application a WHERE a.classCall.semester = :semester")
    List<Integer> findDistinctClassCallIdsBySemester(String semester);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester " +
            "AND a.applicationStatus = :applicationStatus AND a.assignStatus = :assignStatus ORDER BY a.classCall.id ASC")
    List<Application> findApplicationToAutoAssign(String applicationStatus, String assignStatus, String semester);
}
