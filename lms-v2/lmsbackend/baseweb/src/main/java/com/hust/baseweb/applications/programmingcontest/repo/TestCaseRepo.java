package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import com.hust.baseweb.applications.programmingcontest.model.ModelGetTestCaseDetail;
import com.hust.baseweb.applications.programmingcontest.model.TestCaseDetailProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TestCaseRepo extends JpaRepository<TestCaseEntity, UUID> {

    TestCaseEntity findTestCaseByTestCaseId(UUID uuid);

    @Query(
        "SELECT new com.hust.baseweb.applications.programmingcontest.model.ModelGetTestCaseDetail(SUBSTRING(tc.testCase, 1, 30), SUBSTRING(tc.correctAnswer, 1, 30), tc.testCasePoint, tc.testCaseId, tc.isPublic, tc.statusId, tc.description) " +
        "FROM TestCaseEntity tc " +
        "WHERE tc.problemId = ?1 " +
        "AND (tc.statusId IS NULL OR tc.statusId <> 'DISABLED')")
    Page<ModelGetTestCaseDetail> getPreviewByProblemId(String problemId, Pageable pageable);

    @Query(
        "SELECT new com.hust.baseweb.applications.programmingcontest.model.ModelGetTestCaseDetail(tc.testCase, tc.correctAnswer, tc.testCasePoint, tc.testCaseId, tc.isPublic, tc.statusId, tc.description) " +
        "FROM TestCaseEntity tc " +
        "WHERE tc.problemId = ?1 " +
        "AND (tc.statusId IS NULL OR tc.statusId <> 'DISABLED') " +
        "AND (?2 IS NULL OR cast(?2 AS text) = cast(false AS text) OR tc.isPublic = 'Y')")
    Page<ModelGetTestCaseDetail> getFullByProblemId(String problemId, Boolean publicOnly, Pageable pageable);

    List<TestCaseEntity> findAllByProblemId(String problemId);

    List<TestCaseEntity> findAllByProblemIdAndIsPublic(String problemId, String isPublic);

    @Query(value = "select * from test_case_new where contest_problem_id = ?1 and status_id is null or status_id <> 'DISABLED'",
           nativeQuery = true)
    List<TestCaseEntity> findAllActiveTestcaseOfProblem(String problemId);

    @Query(value = "select  " +
                   "    case " +
                   "        when octet_length(test_case) > :threshold then null " +
                   "        else test_case " +
                   "    end as testCase, " +
                   "    correct_answer as correctAns, " +
                   "    test_case_point as point, " +
                   "    cast(test_case_id as text) as testCaseId, " +
                   "    is_public as isPublic, " +
                   "    status_id as status, " +
                   "    description as description " +
                   "from " +
                   "    test_case_new " +
                   "where " +
                   "    test_case_id = :id and (status_id is null or status_id <> 'DISABLED')",
           nativeQuery = true)
    TestCaseDetailProjection getTestCaseDetailByTestCaseId(
        @Param("id") UUID testCaseId,
        @Param("threshold") int threshold
    );
}
