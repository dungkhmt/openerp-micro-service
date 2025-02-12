package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.UserContestProblemRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface UserContestProblemRoleRepo extends JpaRepository<UserContestProblemRole, UUID> {

    List<UserContestProblemRole> findAllByProblemId(String problemId);

    List<UserContestProblemRole> findAllByProblemIdAndUserId(String problemId, String userId);

    List<UserContestProblemRole> findAllByProblemIdAndUserIdAndRoleId(String problemId, String userId, String roleId);

    /*
    @Query(value = "SELECT DISTINCT problem_id FROM user_contest_problem_role WHERE user_id = ?1 AND problem_id NOT IN"
            +
            "(SELECT DISTINCT problem_id FROM user_contest_problem_role WHERE user_id = ?1 AND role_id = 'OWNER')", nativeQuery = true)
    */
    // updated by PQD
    @Query(value = "select distinct problem_id from user_contest_problem_role ucpr  \n" +
                   "where ucpr.user_id  = ?1 and problem_id not in (select problem_id from contest_problem_new cpn where created_by_user_login_id = ?1)", nativeQuery = true)
    List<String> getProblemIdsShared(String userId);

    @Query(value = "select problem_id from contest_problem_new cpn where is_public  = true", nativeQuery = true)
    List<String> getProblemIdsPublic(String userId);


    boolean existsByProblemIdAndUserIdAndRoleId(String problemId, String userId, String roleId);

    @Query(value = "SELECT role_id FROM user_contest_problem_role WHERE problem_id = ?1 AND user_id = ?2", nativeQuery = true)
    List<String> getRolesByProblemIdAndUserId(String problemId, String userId);
}
