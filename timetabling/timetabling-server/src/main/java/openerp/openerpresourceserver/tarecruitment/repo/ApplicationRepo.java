package openerp.openerpresourceserver.tarecruitment.repo;

import openerp.openerpresourceserver.tarecruitment.entity.Application;
import openerp.openerpresourceserver.tarecruitment.entity.ClassCall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepo extends JpaRepository<Application, Integer> {

    @Query("SELECT a FROM Application a WHERE a.user.id = :userId ORDER BY a.classCall.id ASC")
    Page<Application> findByUserId(String userId, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE a.classCall.id = :classCallId ORDER BY a.classCall.id ASC")
    Page<Application> findByClassCallId(int classCallId, Pageable pageable);

    @Query("SELECT a " +
            "FROM Application a " +
            "WHERE a.classCall.semester = :semester " +
            "AND a.applicationStatus = :status " +
            "AND (:search = '' " +
            "     OR (a.classCall.subjectName LIKE CONCAT('%', :search, '%') " +
            "     OR a.name LIKE CONCAT('%', :search, '%') " +
            "     OR CAST(a.classCall.id AS string) LIKE CONCAT('%', :search, '%') " +
            "     OR a.mssv LIKE CONCAT('%', :search, '%'))) " +
            "AND (:assignStatus = '' OR a.assignStatus = :assignStatus) " +
            "ORDER BY a.classCall.id ASC")
    Page<Application> findByApplicationStatusAndSemester(String status, String semester, String search, String assignStatus, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE a.id IN " +
            "(SELECT MAX(a2.id) FROM Application a2 GROUP BY a2.user.id) ORDER BY a.classCall.id ASC")
    List<Application> findDistinctApplicationsByUser();

    @Query("SELECT a " +
            "FROM Application a " +
            "WHERE a.classCall.semester = :semester " +
            "AND (:search = '' " +
            "     OR (a.classCall.subjectName LIKE CONCAT('%', :search, '%') " +
            "     OR a.name LIKE CONCAT('%', :search, '%') " +
            "     OR a.mssv LIKE CONCAT('%', :search, '%')" +
            "     OR CAST(a.classCall.id AS string) LIKE CONCAT('%', :search, '%'))) " +
            "AND (:applicationStatus = '' OR a.applicationStatus = :applicationStatus) " +
            "ORDER BY a.classCall.id ASC")
    Page<Application> findApplicationsByClassSemester(String semester, String search, String applicationStatus, Pageable pageable);

    @Query("SELECT a FROm Application a WHERE a.user.id = :id AND a.assignStatus = :status " +
            "AND a.classCall.semester = :semester ORDER BY a.classCall.id ASC")
    List<Application> findApplicationByUserIdAndAssignStatus(String id, String status, String semester);

    @Query("SELECT DISTINCT a.user.id FROM Application a WHERE a.classCall.semester = :semester AND a.assignStatus = :status " +
            "AND a.applicationStatus = :applicationStatus")
    List<String> findDistinctUserIdsBySemester(String semester, String applicationStatus, String status);

    @Query("SELECT DISTINCT a.classCall FROM Application a WHERE a.classCall.semester = :semester")
    List<ClassCall> findDistinctClassCallBySemester(String semester);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester " +
            "AND a.applicationStatus = :applicationStatus AND a.assignStatus = :assignStatus ORDER BY a.classCall.id ASC")
    List<Application> findApplicationToAutoAssign(String applicationStatus, String assignStatus, String semester);
}
