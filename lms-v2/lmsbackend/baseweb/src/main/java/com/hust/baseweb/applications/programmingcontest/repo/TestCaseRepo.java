package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.TestCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TestCaseRepo extends JpaRepository<TestCaseEntity, UUID> {

    TestCaseEntity findTestCaseByTestCaseId(UUID uuid);

    List<TestCaseEntity> findAllByProblemId(String problemId);

    List<TestCaseEntity> findAllByProblemIdAndStatusId(String problemId, String statusId);

    List<TestCaseEntity> findAllByProblemIdAndIsPublic(String problemId, String isPublic);

    List<TestCaseEntity> findAllByProblemIdAndIsPublicAndStatusId(String problemId, String isPublic, String statusId);

    @Query(value = "select * from test_case_new where contest_problem_id = ?1 and status_id is null or status_id <> 'DISABLED'",
           nativeQuery = true)
    List<TestCaseEntity> findAllActiveTestcaseOfProblem(String problemId);

}
