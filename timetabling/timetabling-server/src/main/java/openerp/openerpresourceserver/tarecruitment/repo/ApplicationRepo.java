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

    @Query("SELECT a FROM Application a WHERE a.classCall.id = :classCallId ORDER BY a.classCall.id ASC, a.user.id ASC")
    Page<Application> findByClassCallId(int classCallId, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE a.user.id = :userId AND a.classCall.id = :classCallId")
    Application findByUserAndClassCall(String userId, int classCallId);

    @Query("SELECT a " +
            "FROM Application a " +
            "WHERE a.classCall.semester = :semester " +
            "AND a.applicationStatus = :status " +
            "AND (:search = '' " +
            "     OR (LOWER(a.classCall.subjectName) LIKE CONCAT('%', LOWER(:search), '%') " +
            "     OR LOWER(a.name) LIKE CONCAT('%', LOWER(:search), '%') " +
            "     OR CAST(a.classCall.id AS string) LIKE CONCAT('%', LOWER(:search), '%') " +
            "     OR LOWER(a.mssv) LIKE CONCAT('%', LOWER(:search), '%'))) " +
            "AND (:assignStatus = '' OR a.assignStatus = :assignStatus) " +
            "ORDER BY a.classCall.id ASC, a.user.id ASC")
    Page<Application> findByApplicationStatusAndSemester(String status, String semester, String search, String assignStatus, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester " +
            "AND a.applicationStatus = :applicationStatus " +
            "AND a.assignStatus = :assignStatus " +
            "AND (:search = '' " +
            "OR LOWER(a.name) LIKE CONCAT('%', LOWER(:search), '%') " +
            "OR LOWER(a.email) LIKE CONCAT('%', LOWER(:search), '%') " +
            "OR LOWER(a.mssv) LIKE CONCAT('%', LOWER(:search), '%') " +
            "OR LOWER(a.classCall.subjectName) LIKE CONCAT('%', LOWER(:search), '%') " +
            "OR LOWER(CAST(a.classCall.id AS string)) LIKE CONCAT('%', LOWER(:search), '%') " +
            "OR LOWER(a.classCall.subjectId) LIKE CONCAT('%', LOWER(:search), '%')) " +
            "ORDER BY a.user.id ASC, a.classCall.id ASC")
    Page<Application> getTABySemester(String applicationStatus, String assignStatus, String semester, String search, Pageable pageable);

    @Query("SELECT a FROM Application a WHERE a.id IN " +
            "(SELECT MAX(a2.id) FROM Application a2 GROUP BY a2.user.id) ORDER BY a.classCall.id ASC, a.user.id ASC")
    List<Application> findDistinctApplicationsByUser();

    @Query("SELECT a " +
            "FROM Application a " +
            "WHERE a.classCall.semester = :semester " +
            "AND (:search = '' " +
            "     OR (LOWER(a.classCall.subjectName) LIKE CONCAT('%', LOWER(:search), '%') " +
            "     OR LOWER(a.name) LIKE CONCAT('%', LOWER(:search), '%') " +
            "     OR LOWER(a.mssv) LIKE CONCAT('%', LOWER(:search), '%')" +
            "     OR LOWER(CAST(a.classCall.id AS string)) LIKE CONCAT('%', LOWER(:search), '%'))) " +
            "AND (:applicationStatus = '' OR a.applicationStatus = :applicationStatus) " +
            "ORDER BY a.classCall.id ASC, a.user.id ASC")
    Page<Application> findApplicationsByClassSemester(String semester, String search, String applicationStatus, Pageable pageable);

    @Query("SELECT a FROm Application a WHERE a.user.id = :id AND a.assignStatus = :status " +
            "AND a.classCall.semester = :semester ORDER BY a.classCall.id ASC, a.user.id ASC")
    List<Application> findApplicationByUserIdAndAssignStatus(String id, String status, String semester);

    @Query("SELECT DISTINCT a.user.id FROM Application a WHERE a.classCall.semester = :semester AND a.assignStatus = :status " +
            "AND a.applicationStatus = :applicationStatus")
    List<String> findDistinctUserIdsBySemester(String semester, String applicationStatus, String status);

    @Query("SELECT DISTINCT a.user.id FROM Application a WHERE a.classCall.semester = :semester")
    List<String> findAllUserBySemester(String semester);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester")
    List<Application> getAllApplicationBySemester(String semester);

    @Query("SELECT DISTINCT a.user.id FROM Application a WHERE a.classCall.semester = :semester AND a.applicationStatus = 'APPROVED' " +
            "AND a.assignStatus = 'APPROVED'")
    List<String> getTADataBySemester(String semester);

    @Query("SELECT DISTINCT a.classCall FROM Application a WHERE a.classCall.semester = :semester")
    List<ClassCall> findDistinctClassCallBySemester(String semester);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester " +
            "AND a.applicationStatus = :applicationStatus AND a.assignStatus = :assignStatus ORDER BY a.classCall.id ASC, a.user.id ASC")
    List<Application> findApplicationToAutoAssign(String applicationStatus, String assignStatus, String semester);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester AND a.applicationStatus = 'APPROVED' AND " +
            "a.user.id != :userId AND a.classCall.id = :classCallId")
    List<Application> getAllRemainingApplication(String semester, String userId, int classCallId);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester AND a.classCall.subjectId = :course")
    List<Application> getApplicationByCourseAndSemester(String semester, String course);

    @Query("SELECT DISTINCT a.name FROM Application a WHERE a.user.id = :userId")
    String getApplicatorName(String userId);

    @Query("SELECT a FROM Application a WHERE a.classCall.semester = :semester AND a.name = :name")
    List<Application> getApplicationByNameAndSemester(String name, String semester);
}
