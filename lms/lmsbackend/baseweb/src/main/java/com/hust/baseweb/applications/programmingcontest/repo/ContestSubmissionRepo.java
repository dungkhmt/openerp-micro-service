package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.admin.dataadmin.education.model.ProgrammingContestSubmissionOM;
import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import com.hust.baseweb.applications.programmingcontest.model.ModelProblemMaxSubmissionPoint;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ContestSubmissionRepo extends JpaRepository<ContestSubmissionEntity, UUID> {
//    List<Integer> getListProblemSubmissionDistinctWithHighestScore(@Param("userLogin") UserLogin userLogin);

    @Query(value = "select user_submission_id as userId, sum(p) as point, email, first_name, middle_name, last_name from " +
            "( select user_submission_id, problem_id , max(point) as p, ul.email as email, person.first_name as first_name, person.middle_name as middle_name, person.last_name as last_name " +
            "from contest_submission_new csn " +
            "inner join user_login ul " +
            "on user_submission_id in (select user_id from user_registration_contest_new urcn where urcn.contest_id=:contest_id and status='SUCCESSFUL') " +
            "and csn.contest_id=:contest_id " +
            "and csn.user_submission_id = ul.user_login_id " +
            "inner join person  " +
            "on person.party_id = ul.party_id " +
            "group by problem_id, user_submission_id, ul.email, problem_id, user_submission_id, person.first_name, person.middle_name, person.last_name) " +
            "as cur group by user_submission_id, email, first_name, middle_name, last_name order by point desc "
            ,
            nativeQuery = true
    )
    List<Object[]> calculatorContest(@Param("contest_id") String contest_id);

    @Query(value = "select distinct problem_id from contest_submission_new " +
                   "where user_submission_id = :user_id " +
                   "and contest_id = :contest_id " +
                   "and status = 'Accept'"
        ,
           nativeQuery = true
    )
    List<String> findAcceptedProblemsOfUser(@Param("user_id") String user_id, @Param("contest_id") String contest_id);

    @Query(value = "select problem_id as problemId, max(point) as maxPoint from contest_submission_new " +
                   "where user_submission_id = :user_id " +
                   "and contest_id = :contest_id " +
                   "group by problemId "
        ,
           nativeQuery = true
    )
    List<ModelProblemMaxSubmissionPoint> findSubmittedProblemsOfUser(@Param("user_id") String user_id, @Param("contest_id") String contest_id);

    ContestSubmissionEntity findContestSubmissionEntityByContestSubmissionId(UUID contestSubmissionId);

    @Query(value = "select * from contest_submission_new csn where csn.contest_id = :cid and csn.user_submission_id=:uid and csn.problem_id=:pid" +
                   " order by created_stamp desc "
        ,
           nativeQuery = true
    )
    List<ContestSubmissionEntity> findAllByContestIdAndUserIdAndProblemId(@Param("cid")String cid, @Param("uid")String uid, @Param("pid")String pid);

    @Query(value = "select count(*) from contest_submission_new csn where csn.contest_id = :cid and csn.user_submission_id=:uid and csn.problem_id=:pid"
        ,
           nativeQuery = true
    )
    int countAllByContestIdAndUserIdAndProblemId(@Param("cid")String cid, @Param("uid")String uid, @Param("pid")String pid);


    @Query(value = "select * from contest_submission_new csn where csn.contest_id = :cid and csn.status = :status" +
                   " order by created_stamp asc "
        ,
           nativeQuery = true
    )
    List<ContestSubmissionEntity> findAllByContestIdAndStatus(@Param("cid")String cid, @Param("status")String status);

    List<ContestSubmissionEntity> findAllByContestId(String contestId);

    void deleteAllByContestId(String contestId);

    @Query(value="select count(*) from contest_submission_new",nativeQuery=true)
    int countTotal();

    @Query(
        nativeQuery = true,
        value = "SELECT ct.contest_id contestId, ct.contest_name contestName, p.problem_id problemId, p.problem_name problemName, " +
                    "CAST(cts.contest_submission_id as VARCHAR(64)) submissionId, cts.status status, cts.test_case_pass testCasePass, cts.point point, " +
                    "cts.source_code_language sourceCodeLanguage, cts.created_stamp submitAt " +
                "FROM contest_submission_new cts " +
                    "INNER JOIN contest_new ct ON cts.user_submission_id = :studentLoginId AND cts.contest_id = ct.contest_id " +
                    "INNER JOIN contest_problem_new p ON cts.problem_id = p.problem_id " +
                "WHERE LOWER(ct.contest_name) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(p.problem_name) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(cts.contest_submission_id as VARCHAR)) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(cts.status) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(cts.test_case_pass) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(cts.point as VARCHAR(10))) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(cts.source_code_language) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(cts.created_stamp as VARCHAR)) LIKE CONCAT('%', LOWER(:search), '%')"

    )
    Page<ProgrammingContestSubmissionOM> findContestSubmissionsOfStudent(
        @Param("studentLoginId") String studentLoginId,
        @Param("search") String search,
        Pageable pageable
    );

    @Modifying
    @Query("update ContestSubmissionEntity s set s.status = ?2 where s.contestSubmissionId = ?1")
    void updateContestSubmissionStatus(UUID contestSubmissionId, String status);
}
