package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.UserContestProblemRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
public interface UserContestProblemRoleRepo extends JpaRepository<UserContestProblemRole, UUID>{
    List<UserContestProblemRole> findAllByProblemId(String problemId);
    List<UserContestProblemRole> findAllByProblemIdAndUserId(String problemId, String userId);
    List<UserContestProblemRole> findAllByProblemIdAndUserIdAndRoleId(String problemId, String userId, String roleId);
}
