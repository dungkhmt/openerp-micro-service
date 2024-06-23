package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.ContestSubmission;
import openerp.openerpresourceserver.model.StudentSubmissionBySemester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ContestSubmissionRepo extends JpaRepository<ContestSubmission, String> {

    @Query("SELECT cs FROM ContestSubmission cs WHERE cs.userSubmissionId = :userId ORDER BY cs.createdDate ASC")
    List<ContestSubmission> findAllByUserSubmissionIdOrderByCreatedDateAsc(@Param("userId") String userId);
    List<ContestSubmission> findByContestIdAndUserSubmissionId(String contestId, String studentId);

    long count();

    @Query(value = "SELECT COUNT( distinct csn.problem_id) FROM contest_submission_new csn ", nativeQuery = true)
    long countAllProblemIds();

    @Query(value =
            "SELECT " +
            "s.problem_id, " +
            "COUNT(*) as submission_count, " +
            "(SELECT test_case_pass " +
            " FROM contest_submission_new " +
            " WHERE user_submission_id = s.user_submission_id " +
            " AND problem_id = s.problem_id " +
            " ORDER BY point DESC " +
            " LIMIT 1) AS max_submission_score, " +
            "(SELECT test_case_pass " +
            " FROM contest_submission_new " +
            " WHERE user_submission_id = s.user_submission_id " +
            " AND problem_id = s.problem_id " +
            " ORDER BY created_stamp " +
            " LIMIT 1) AS first_submission_score " +
            "FROM contest_submission_new s " +
            "WHERE  " +
            "    user_submission_id = :userId " +
            "GROUP BY s.user_submission_id, s.problem_id " +
            "ORDER BY s.user_submission_id, s.problem_id", nativeQuery = true)

    Object[] findScoreChangesInSubmissionByUserId(@Param("userId") String userId);

    @Query(value =
            "SELECT " +
            "CASE " +
            "    WHEN source_code_language LIKE '%CPP%' THEN 'CPP' " +
            "    WHEN source_code_language LIKE '%PYTHON%' THEN 'Python' " +
            "    WHEN source_code_language LIKE '%JAVA%' THEN 'Java' " +
            "    ELSE source_code_language " +
            "END AS grouped_language, " +
            "COUNT(*) AS submission_count " +
            "FROM contest_submission_new " +
            "WHERE user_submission_id = :userId " +
            "GROUP BY user_submission_id, grouped_language " +
            "ORDER BY user_submission_id, submission_count DESC",nativeQuery = true)
    Object[] findNumberCountLanguagesDetailByUserId(@Param("userId") String userId);

    @Query(value =
            "WITH ranked_submissions AS ( " +
                    "    SELECT " +
                    "        user_submission_id, " +
                    "        count(*) AS number_submission, " +
                    "        EXTRACT(MONTH FROM created_stamp) AS submission_month, " +
                    "        CASE " +
                    "            WHEN EXTRACT(MONTH FROM created_stamp) >= 10 THEN 'Kỳ ' || EXTRACT(YEAR FROM created_stamp)  || 1\n" +
                    "            WHEN EXTRACT(MONTH FROM created_stamp) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM created_stamp) - 1 || 1\n" +
                    "            WHEN EXTRACT(MONTH FROM created_stamp) >= 4 AND EXTRACT(MONTH FROM created_stamp) <= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM created_stamp) -1 || 2\n" +
                    "        END AS semester, " +
                    "        RANK() OVER (PARTITION BY user_submission_id ORDER BY count(*) DESC) AS rank " +
                    "    FROM contest_submission_new\n" +
                    "    WHERE upper(contest_id) NOT LIKE upper('%Midterm%') AND upper(contest_id) NOT LIKE upper('%Final%') " +
                    "    GROUP BY user_submission_id, semester, submission_month " +
                    ")\n" +
                    "select rs.semester " +
                    "FROM ranked_submissions rs " +
                    "WHERE rs.rank = 1 and rs.user_submission_id = :userId ", nativeQuery = true)
    String findSemester(@Param("userId") String userId);

    @Query(value =
            "WITH RankedSubmissions AS ( " +
                "SELECT " +
                    "userSubmissionId as userSubmissionId, " +
                    "count(*) AS numberSubmission, " +
                    "EXTRACT(MONTH FROM createdDate) AS submissionMonth, " +
                    "CASE " +
                        "WHEN EXTRACT(MONTH FROM createdDate) >= 10 THEN 'Kỳ ' || EXTRACT(YEAR FROM createdDate) || 1 " +
                        "WHEN EXTRACT(MONTH FROM createdDate) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM createdDate) - 1 || 1 " +
                        "WHEN EXTRACT(MONTH FROM createdDate) >= 4 AND EXTRACT(MONTH FROM createdDate) <= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM createdDate) - 1 || 2 " +
                    "END AS semester, " +
                    "RANK() OVER (PARTITION BY userSubmissionId ORDER BY count(*) DESC) AS rank " +
                "FROM ContestSubmission " +
                "WHERE  upper(contestId) NOT LIKE upper('%Midterm%') AND upper(contestId) NOT LIKE upper('%Final%') " +
                "GROUP BY userSubmissionId, semester, submissionMonth " +
            ") " +
            "SELECT new openerp.openerpresourceserver.model.StudentSubmissionBySemester (  " +
                "r.userSubmissionId, " +
                "r.semester, " +
                "r.submissionMonth, " +
                "r.numberSubmission, " +
                "0.0 as submissionDeviationByMonth ) " +
            "FROM RankedSubmissions r " +
            "WHERE r.userSubmissionId = :userId and r.semester= :semesterMax  " +
            "ORDER BY r.submissionMonth " )
    List<StudentSubmissionBySemester> findSemesterSubmissionHaveMaxSubmission(@Param("userId") String userId, @Param("semesterMax") String semesterMax);

    @Query(value =
            "WITH midterm_point AS ( "
            + "SELECT "
            + "mfs.user_submission_id, "
            + "mfs.contest_id, "
            + "semester, "
            + "CAST(SUM(mfs.point) * 10.0 / mpc.total_point AS DECIMAL(15, 2)) AS midterm_point "
            + "FROM "
            + "midterm_final_submission_view mfs "
            + "INNER JOIN "
            + "max_point_contest_view mpc ON mfs.contest_id = mpc.contest_id "
            + "WHERE "
            + "semester IS NOT NULL "
            + "AND UPPER(mfs.contest_id) LIKE UPPER('%midterm%') "
            + "GROUP BY "
            + "mfs.user_submission_id, mfs.contest_id, semester, mpc.total_point "
            + "), "
            + "final_point AS ( "
            + "SELECT "
            + "mfs.user_submission_id, "
            + "mfs.contest_id, "
            + "semester, "
            + "CAST(SUM(mfs.point) * 10.0 / mpc.total_point AS DECIMAL(15, 2)) AS final_point "
            + "FROM "
            + "midterm_final_submission_view mfs "
            + "INNER JOIN "
            + "max_point_contest_view mpc ON mfs.contest_id = mpc.contest_id "
            + "WHERE "
            + "semester IS NOT NULL "
            + "AND UPPER(mfs.contest_id) LIKE UPPER('%final%') "
            + "GROUP BY "
            + "mfs.user_submission_id, mfs.contest_id, semester, mpc.total_point "
            + ") "
            + "SELECT "
            + "mp.user_submission_id, "
            + "mp.semester, "
            + "COALESCE(mp.midterm_point, 0) AS midterm_point, "
            + "COALESCE(fp.final_point, 0) AS final_point, "
            + "CASE "
            + "WHEN EXISTS ( "
            + "SELECT 1 "
            + "FROM code_plagiarism cp "
            + "WHERE mp.user_submission_id = cp.user_id_1 "
            + "AND mp.contest_id = cp.contest_id "
            + ") THEN "
            + "CASE "
            + "WHEN UPPER(mp.contest_id) LIKE '%MIDTERM%' THEN 1 "
            + "WHEN UPPER(mp.contest_id) LIKE '%FINAL%' THEN 2 "
            + "END "
            + "ELSE 0 "
            + "END AS appearedInPlagiarism, "
            + "CASE "
            + "WHEN COALESCE(mp.midterm_point, 0) < 2.5 "
            + "OR COALESCE(fp.final_point, 0) < 2.5 "
            + "OR (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) <= 3.5 THEN -1 "
            + "ELSE 1 "
            + "END AS evaluation_result "
            + "FROM "
            + "midterm_point mp "
            + "FULL OUTER JOIN "
            + "final_point fp ON mp.user_submission_id = fp.user_submission_id "
            + "WHERE mp.user_submission_id = :userId "
            + "ORDER BY mp.semester", nativeQuery = true)
    Object[] findStudentSemesterResult(@Param("userId") String userId);

    @Query(value =
            "WITH midterm_point AS (\n" +
                    "SELECT mfs.user_submission_id, mfs.contest_id, semester, CAST(SUM(mfs.point) * 10.0 / mpc.total_point AS DECIMAL(15, 2)) AS midterm_point \n" +
                    "FROM midterm_final_submission_view mfs\n" +
                    "INNER JOIN max_point_contest_view mpc ON mfs.contest_id = mpc.contest_id \n" +
                    "WHERE semester IS NOT NULL AND UPPER(mfs.contest_id) LIKE UPPER('%midterm%')\n" +
                    "GROUP BY mfs.user_submission_id, mfs.contest_id, semester, total_point\n" +
                    "),\n" +
                    "final_point AS (\n" +
                    "SELECT mfs.user_submission_id, mfs.contest_id, semester, CAST(SUM(mfs.point) * 10.0 / mpc.total_point AS DECIMAL(15, 2)) AS final_point \n" +
                    "FROM midterm_final_submission_view mfs\n" +
                    "INNER JOIN max_point_contest_view mpc ON mfs.contest_id = mpc.contest_id \n" +
                    "WHERE semester IS NOT NULL AND UPPER(mfs.contest_id) LIKE UPPER('%final%')\n" +
                    "GROUP BY mfs.user_submission_id, mfs.contest_id, semester, total_point\n" +
                    "),\n" +
                    "state AS (\n" +
                    "SELECT \n" +
                    "    mp.user_submission_id,\n" +
                    "    mp.semester,\n" +
                    "    COALESCE(mp.midterm_point, 0) AS midterm_point,\n" +
                    "    COALESCE(fp.final_point, 0) AS final_point,\n" +
                    "    (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) AS final_grade,\n" +
                    "    CASE \n" +
                    "        WHEN midterm_point < 2.5 OR final_point < 2.5 OR (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) <= 3.5 THEN 'failed'\n" +
                    "        ELSE 'pass'\n" +
                    "    END AS evaluation_result\n" +
                    "FROM \n" +
                    "    midterm_point mp\n" +
                    "FULL OUTER JOIN \n" +
                    "    final_point fp ON mp.user_submission_id = fp.user_submission_id\n" +
                    ")\n" +
                    "SELECT s.semester, COALESCE(COUNT(user_submission_id), 0) AS total_student_pass\n" +
                    "FROM (SELECT DISTINCT semester FROM state) s\n" +
                    "LEFT JOIN state ON s.semester = state.semester AND state.evaluation_result = 'pass'\n" +
                    "GROUP BY s.semester " +
                    "ORDER BY s.semester "
            , nativeQuery = true)
    Object[] findResultAllSemester();

    @Query(value =
            "WITH midterm_point AS ( " +
            "SELECT " +
            "mfs.user_submission_id, " +
            "mfs.contest_id, " +
            "semester, " +
            "CAST(SUM(mfs.point) * 10.0 / mpc.total_point AS DECIMAL(15, 2)) AS midterm_point " +
            "FROM " +
            "midterm_final_submission_view mfs " +
            "INNER JOIN " +
            "max_point_contest_view mpc ON mfs.contest_id = mpc.contest_id " +
            "WHERE " +
            "semester IS NOT NULL " +
            "AND UPPER(mfs.contest_id) LIKE UPPER('%midterm%') " +
            "GROUP BY " +
            "mfs.user_submission_id, mfs.contest_id, semester, mpc.total_point " +
            "), " +
            "final_point AS ( " +
            "SELECT " +
            "mfs.user_submission_id, " +
            "mfs.contest_id, " +
            "semester, " +
            "CAST(SUM(mfs.point) * 10.0 / mpc.total_point AS DECIMAL(15, 2)) AS final_point " +
            "FROM " +
            "midterm_final_submission_view mfs " +
            "INNER JOIN " +
            "max_point_contest_view mpc ON mfs.contest_id = mpc.contest_id " +
            "WHERE " +
            "semester IS NOT NULL " +
            "AND UPPER(mfs.contest_id) LIKE UPPER('%final%') " +
            "GROUP BY " +
            "mfs.user_submission_id, mfs.contest_id, semester, mpc.total_point " +
            ") " +
            "SELECT " +
            "mp.semester, " +
            "CASE " +
            "WHEN COALESCE(mp.midterm_point, 0) < 2.5 " +
            "OR COALESCE(fp.final_point, 0) < 2.5 " +
            "OR (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) <= 3.5 THEN 'F' " +
            "WHEN (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) > 8.4 THEN 'A/A+' " +
            "WHEN (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) > 6.9 THEN 'B/B+' " +
            "WHEN (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) > 5.4 THEN 'C/C+' " +
            "ELSE 'D/D+' " +
            "END AS evaluation_result, " +
            "COUNT(*) AS num_students " +
            "FROM " +
            "midterm_point mp " +
            "FULL OUTER JOIN " +
            "final_point fp ON mp.user_submission_id = fp.user_submission_id " +
            "GROUP BY " +
            "mp.semester, " +
            "CASE " +
            "WHEN COALESCE(mp.midterm_point, 0) < 2.5 " +
            "OR COALESCE(fp.final_point, 0) < 2.5 " +
            "OR (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) <= 3.5 THEN 'F' " +
            "WHEN (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) > 8.4 THEN 'A/A+' " +
            "WHEN (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) > 6.9 THEN 'B/B+' " +
            "WHEN (0.4 * COALESCE(mp.midterm_point, 0)) + (0.6 * COALESCE(fp.final_point, 0)) > 5.4 THEN 'C/C+' " +
            "ELSE 'D/D+' " +
            "END " +
            "ORDER BY mp.semester DESC ", nativeQuery = true)
    Object[] numberScoreBySemester();
}
