package openerp.openerpresourceserver.tarecruitment.repo;

import openerp.openerpresourceserver.tarecruitment.entity.ClassCall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassCallRepo extends JpaRepository<ClassCall, Integer> {
    @Query("SELECT cc FROM ClassCall cc " +
            "WHERE cc.semester = :semester " +
            "AND (:search = '' " +
            "     OR LOWER(cc.subjectName) LIKE CONCAT('%', LOWER(:search), '%') " +
            "     OR LOWER(cc.subjectId) LIKE CONCAT('%', LOWER(:search), '%') " +
            "     OR LOWER(CAST(cc.id AS string)) LIKE CONCAT('%', LOWER(:search), '%')) " +
            "ORDER BY cc.id ASC")
    Page<ClassCall> findBySemester(String semester, String search, Pageable pageable);

    @Query("SELECT cc FROM ClassCall cc, Application a WHERE cc.id = a.classCall.id AND a.user.id = :userId " +
            "AND a.classCall.semester = :semester")
    List<ClassCall> getAllMyRegisteredClass(String userId, String semester);

    @Query("SELECT cc FROM ClassCall cc WHERE cc.semester = :semester")
    List<ClassCall> findAllBySemester(String semester);

    @Query("SELECT DISTINCT cc.subjectId FROM ClassCall cc WHERE cc.semester = :semester")
    List<String> getDistinctCourseBySemester(String semester);
}
