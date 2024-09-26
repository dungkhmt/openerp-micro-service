package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.ClassCall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassCallRepo extends JpaRepository<ClassCall, Integer> {
    @Query("SELECT cc FROM ClassCall cc " +
            "WHERE cc.semester = :semester " +
            "AND (:search = '' " +
            "     OR cc.subjectName LIKE CONCAT('%', :search, '%') " +
            "     OR cc.subjectId LIKE CONCAT('%', :search, '%') " +
            "     OR CAST(cc.id AS string) LIKE CONCAT('%', :search, '%')) " +
            "ORDER BY cc.id ASC")
    Page<ClassCall> findBySemester(String semester, String search, Pageable pageable);

    @Query("SELECT cc FROM ClassCall cc, Application a WHERE cc.id = a.classCall.id AND a.user.id = :userId " +
            "AND a.classCall.semester = :semester")
    List<ClassCall> getAllMyRegisteredClass(String userId, String semester);
}
