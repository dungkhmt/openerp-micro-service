package openerp.openerpresourceserver.repo;

import openerp.openerpresourceserver.entity.ContestSubmission;
import openerp.openerpresourceserver.model.StudentSemesterResult;
import openerp.openerpresourceserver.model.StudentSubmissionBySemster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ContestSubmissionRepo extends JpaRepository<ContestSubmission, String> {

    List<ContestSubmission> findByUserSubmissionId(String studentId);
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
            "WHERE point > 0 and user_submission_id = :userId " +
            "GROUP BY user_submission_id, grouped_language " +
            "ORDER BY user_submission_id, submission_count DESC",nativeQuery = true)
    Object[] findNumberCountLanguagesDetailByUserId(@Param("userId") String userId);

    @Query(value =
            "WITH MidtermFinalSemesters AS ( " +
                    "  SELECT DISTINCT " +
                    "    user_submission_id, " +
                    "    CASE " +
                    "       WHEN EXTRACT(MONTH FROM created_stamp) >= 10 THEN 'Kỳ ' || EXTRACT(YEAR FROM created_stamp) || '1' " +
                    "       WHEN EXTRACT(MONTH FROM created_stamp) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM created_stamp) - 1 || '1' " +
                    "       WHEN EXTRACT(MONTH FROM created_stamp) >= 4 AND EXTRACT(MONTH FROM created_stamp) <= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM created_stamp) - 1 || '2' " +
                    "    END AS semester " +
                    "  FROM contest_submission_new " +
                    "  WHERE UPPER(contest_id) LIKE '%MIDTERM%' OR UPPER(contest_id) LIKE '%FINAL%' " +
                    "  UNION ALL " +
                    "  SELECT DISTINCT " +
                    "    user_submission_id, " +
                    "    CASE " +
                    "       WHEN EXTRACT(MONTH FROM CURRENT_DATE) >= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM CURRENT_DATE) || '1' " +
                    "       WHEN EXTRACT(MONTH FROM CURRENT_DATE) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM CURRENT_DATE) - 1 || '1' " +
                    "       WHEN EXTRACT(MONTH FROM CURRENT_DATE) >= 3 AND EXTRACT(MONTH FROM CURRENT_DATE) <= 9 THEN 'Kỳ ' || EXTRACT(YEAR FROM CURRENT_DATE) - 1 || '2' " +
                    "    END AS semester " +
                    "  FROM contest_submission_new " +
                    "  WHERE user_submission_id NOT IN ( " +
                    "      SELECT user_submission_id " +
                    "      FROM contest_submission_new " +
                    "      WHERE UPPER(contest_id) LIKE '%midterm%' AND UPPER(contest_id) NOT LIKE '%demo%' " +
                    "  ) " +
                    "), " +
                    "StudentSubmissionSemester AS ( " +
                    "  SELECT " +
                    "      mfs.semester, " +
                    "      csn.* " +
                    "  FROM contest_submission_new csn " +
                    "  JOIN MidtermFinalSemesters mfs ON csn.user_submission_id = mfs.user_submission_id " +
                    "      AND mfs.semester = CASE " +
                    "                             WHEN EXTRACT(MONTH FROM csn.created_stamp) >= 10 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.created_stamp) || '1' " +
                    "                             WHEN EXTRACT(MONTH FROM csn.created_stamp) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.created_stamp) - 1 || '1' " +
                    "                             WHEN EXTRACT(MONTH FROM csn.created_stamp) >= 4 AND EXTRACT(MONTH FROM csn.created_stamp) <= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.created_stamp) - 1 || '2' " +
                    "                         end " +
                    "), " +
            "maxp_point_student AS (" +
                    "   SELECT " +
                    "       semester, " +
                    "       user_submission_id, " +
                    "       MAX(point) AS point, " +
                    "       problem_id, " +
                    "       contest_id " +
                    "   FROM " +
                    "       StudentSubmissionSemester csn " +
                    "   WHERE " +
                    "       UPPER(csn.contest_id) LIKE UPPER('%midterm%') AND UPPER(csn.contest_id) NOT LIKE UPPER('%demo%') " +
                    "   GROUP BY " +
                    "       user_submission_id, contest_id, problem_id, semester " +
                    "), " +
                    "max_point AS (" +
                    "   SELECT " +
                    "       ccpn.contest_id, " +
                    "       SUM(test_case_point) AS total_point " +
                    "   FROM " +
                    "       test_case_new tsn " +
                    "   INNER JOIN " +
                    "       contest_contest_problem_new ccpn ON tsn.contest_problem_id = ccpn.problem_id " +
                    "   GROUP BY " +
                    "       ccpn.contest_id " +
                    "   ORDER BY " +
                    "       ccpn.contest_id" +
                    ") " +
                    "SELECT CAST(SUM(mps.point) * 10.0 / total_point AS DECIMAL(10, 1)) AS average_point " +
                    "FROM " +
                    "    maxp_point_student mps " +
                    "INNER JOIN " +
                    "    max_point mp ON mps.contest_id = mp.contest_id " +
                    "WHERE " +
                    "    mps.user_submission_id = :userId " +
                    "GROUP BY " +
                    "    mps.user_submission_id, mps.contest_id, total_point, semester",
            nativeQuery = true)
    List<Double> findMidtermPointByUserId(@Param("userId") String userId);

    @Query(value =
            "WITH MidtermFinalSemesters AS ( " +
                    "  SELECT DISTINCT " +
                    "    user_submission_id, " +
                    "    CASE " +
                    "       WHEN EXTRACT(MONTH FROM created_stamp) >= 10 THEN 'Kỳ ' || EXTRACT(YEAR FROM created_stamp) || '1' " +
                    "       WHEN EXTRACT(MONTH FROM created_stamp) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM created_stamp) - 1 || '1' " +
                    "       WHEN EXTRACT(MONTH FROM created_stamp) >= 4 AND EXTRACT(MONTH FROM created_stamp) <= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM created_stamp) - 1 || '2' " +
                    "    END AS semester " +
                    "  FROM contest_submission_new " +
                    "  WHERE UPPER(contest_id) LIKE '%MIDTERM%' OR UPPER(contest_id) LIKE '%FINAL%' " +
                    "  UNION ALL " +
                    "  SELECT DISTINCT " +
                    "    user_submission_id, " +
                    "    CASE " +
                    "       WHEN EXTRACT(MONTH FROM CURRENT_DATE) >= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM CURRENT_DATE) || '1' " +
                    "       WHEN EXTRACT(MONTH FROM CURRENT_DATE) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM CURRENT_DATE) - 1 || '1' " +
                    "       WHEN EXTRACT(MONTH FROM CURRENT_DATE) >= 3 AND EXTRACT(MONTH FROM CURRENT_DATE) <= 9 THEN 'Kỳ ' || EXTRACT(YEAR FROM CURRENT_DATE) - 1 || '2' " +
                    "    END AS semester " +
                    "  FROM contest_submission_new " +
                    "  WHERE user_submission_id NOT IN ( " +
                    "      SELECT user_submission_id " +
                    "      FROM contest_submission_new " +
                    "      WHERE UPPER(contest_id) LIKE '%final%' AND UPPER(contest_id) NOT LIKE '%demo%' " +
                    "  ) " +
                    "), " +
                    "StudentSubmissionSemester AS ( " +
                    "  SELECT " +
                    "      mfs.semester, " +
                    "      csn.* " +
                    "  FROM contest_submission_new csn " +
                    "  JOIN MidtermFinalSemesters mfs ON csn.user_submission_id = mfs.user_submission_id " +
                    "      AND mfs.semester = CASE " +
                    "                             WHEN EXTRACT(MONTH FROM csn.created_stamp) >= 10 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.created_stamp) || '1' " +
                    "                             WHEN EXTRACT(MONTH FROM csn.created_stamp) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.created_stamp) - 1 || '1' " +
                    "                             WHEN EXTRACT(MONTH FROM csn.created_stamp) >= 4 AND EXTRACT(MONTH FROM csn.created_stamp) <= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.created_stamp) - 1 || '2' " +
                    "                         end " +
                    "), " +
                    "maxp_point_student AS (" +
                    "   SELECT " +
                    "       semester, " +
                    "       user_submission_id, " +
                    "       MAX(point) AS point, " +
                    "       problem_id, " +
                    "       contest_id " +
                    "   FROM " +
                    "       StudentSubmissionSemester csn " +
                    "   WHERE " +
                    "       UPPER(csn.contest_id) LIKE UPPER('%final%') AND UPPER(csn.contest_id) NOT LIKE UPPER('%demo%') " +
                    "   GROUP BY " +
                    "       user_submission_id, contest_id, problem_id, semester " +
                    "), " +
                    "max_point AS (" +
                    "   SELECT " +
                    "       ccpn.contest_id, " +
                    "       SUM(test_case_point) AS total_point " +
                    "   FROM " +
                    "       test_case_new tsn " +
                    "   INNER JOIN " +
                    "       contest_contest_problem_new ccpn ON tsn.contest_problem_id = ccpn.problem_id " +
                    "   GROUP BY " +
                    "       ccpn.contest_id " +
                    "   ORDER BY " +
                    "       ccpn.contest_id" +
                    ") " +
                    "SELECT CAST(SUM(mps.point) * 10.0 / total_point AS DECIMAL(10, 1)) AS average_point " +
                    "FROM " +
                    "    maxp_point_student mps " +
                    "INNER JOIN " +
                    "    max_point mp ON mps.contest_id = mp.contest_id " +
                    "WHERE " +
                    "    mps.user_submission_id = :userId " +
                    "GROUP BY " +
                    "    mps.user_submission_id, mps.contest_id, total_point, semester",
            nativeQuery = true)
    List<Double> findFinalPointByUserId(@Param("userId") String userId);

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
            "SELECT new openerp.openerpresourceserver.model.StudentSubmissionBySemster (  " +
                "r.userSubmissionId, " +
                "r.semester, " +
                "r.submissionMonth, " +
                "r.numberSubmission, " +
                "0.0 as submissionDeviationByMonth ) " +
            "FROM RankedSubmissions r " +
            "WHERE r.userSubmissionId = :userId and r.semester= :semesterMax  " +
            "ORDER BY r.submissionMonth " )
    List<StudentSubmissionBySemster> findSemesterSubmissionHaveMaxSubmission(@Param("userId") String userId, @Param("semesterMax") String semesterMax);

    @Query(value =
            "SELECT new openerp.openerpresourceserver.model.StudentSemesterResult( " +
                    "  mfs.semester AS semester, " +
                    "  csn.userSubmissionId AS userSubmissionId, " +
                    "  COUNT(csn.id) AS totalSubmission, " +
                    "  COUNT(DISTINCT csn.problemId) AS totalProblem, " +
                    " MAX(CASE " +
                    "        WHEN EXISTS ( " +
                    "            SELECT 1 " +
                    "            FROM CodePlagiarism cp " +
                    "            WHERE cp.submissionId1 = csn.id or cp.submissionId2 = csn.id " +
                    "        ) THEN " +
                    "            CASE " +
                    "                WHEN UPPER(csn.contestId) LIKE '%MIDTERM%' THEN 1 " +
                    "                WHEN UPPER(csn.contestId) LIKE '%FINAL%' THEN 2 " +
                    "            END " +
                    "        ELSE 0 " +
                    "    end) AS appearedInPlagiarism, " +
                    "  0.0 as midtermPoint, " +
                    "  0.0 as finalPoint, " +
                    "  0 as finalState, " +
                    "  0.0 as passingRate ) " +
                    "FROM ContestSubmission csn " +
                    "JOIN ( " +
                    "  SELECT DISTINCT csn.userSubmissionId AS userSubmissionId, " +
                    "    CASE WHEN EXTRACT(MONTH FROM csn.createdDate) >= 10 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.createdDate) || '1' " +
                    "         WHEN EXTRACT(MONTH FROM csn.createdDate) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.createdDate) - 1 || '1' " +
                    "         WHEN EXTRACT(MONTH FROM csn.createdDate) >= 4 AND EXTRACT(MONTH FROM csn.createdDate) <= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.createdDate) - 1 || '2' " +
                    "    END AS semester " +
                    "  FROM ContestSubmission csn " +
                    "  WHERE UPPER(csn.contestId) LIKE '%MIDTERM%' OR UPPER(csn.contestId) LIKE '%FINAL%' " +
                    "  UNION ALL " +
                    "  SELECT DISTINCT csn.userSubmissionId AS userSubmissionId, " +
                    "    CASE WHEN EXTRACT(MONTH FROM CURRENT_DATE) >= 10 THEN 'Kỳ ' || EXTRACT(YEAR FROM CURRENT_DATE) || '1' " +
                    "         WHEN EXTRACT(MONTH FROM CURRENT_DATE) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM CURRENT_DATE) - 1 || '1' " +
                    "         WHEN EXTRACT(MONTH FROM CURRENT_DATE) >= 4 AND EXTRACT(MONTH FROM CURRENT_DATE) <= 9 THEN 'Kỳ ' || EXTRACT(YEAR FROM CURRENT_DATE) - 1 || '2' " +
                    "    END AS semester " +
                    "  FROM ContestSubmission csn " +
                    "  WHERE csn.userSubmissionId NOT IN ( " +
                    "      SELECT userSubmissionId FROM ContestSubmission WHERE UPPER(contestId) LIKE '%MIDTERM%' OR UPPER(contestId) LIKE '%FINAL%' " +
                    "  ) " +
                    ") AS mfs ON csn.userSubmissionId = mfs.userSubmissionId " +
                    "WHERE  csn.userSubmissionId = :userId " +
                    "      AND mfs.semester = CASE " +
                    "                             WHEN EXTRACT(MONTH FROM csn.createdDate) >= 10 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.createdDate) || '1' " +
                    "                             WHEN EXTRACT(MONTH FROM csn.createdDate) <= 3 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.createdDate) - 1 || '1' " +
                    "                             WHEN EXTRACT(MONTH FROM csn.createdDate) >= 4 AND EXTRACT(MONTH FROM csn.createdDate) <= 8 THEN 'Kỳ ' || EXTRACT(YEAR FROM csn.createdDate) - 1 || '2' " +
                    "                         end " +
                    "GROUP BY mfs.semester, csn.userSubmissionId " +
                    "ORDER BY mfs.semester DESC ")
    List<StudentSemesterResult> findStudentSemesterResult(@Param("userId") String userId);

}