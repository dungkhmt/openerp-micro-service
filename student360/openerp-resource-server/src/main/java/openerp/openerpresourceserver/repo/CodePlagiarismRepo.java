package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.CodePlagiarism;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodePlagiarismRepo extends JpaRepository<CodePlagiarism, String> {

    @Query(value =
            "SELECT semester, COUNT(DISTINCT cp.user_id_1) AS num_students_copied " +
            "FROM midterm_final_submission_view mfs " +
            "LEFT JOIN code_plagiarism cp ON mfs.user_submission_id = cp.user_id_1 AND mfs.contest_id = cp.contest_id " +
            "WHERE semester IS NOT NULL " +
            "GROUP BY semester " +
            "ORDER BY semester ", nativeQuery = true)
    Object[] countStudentPlagiarismBySemester();

}
