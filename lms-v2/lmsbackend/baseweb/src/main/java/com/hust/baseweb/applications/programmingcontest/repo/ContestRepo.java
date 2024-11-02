package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

public interface ContestRepo extends JpaRepository<ContestEntity, String> {

    ContestEntity findContestByContestId(String contestId);

    List<ContestEntity> findByContestIdInAndStatusIdNot(Set<String> ids, String statusId);

    ContestEntity findContestEntityByContestIdAndUserId(String contestId, String userId);

    @Modifying
    @Query(value = "update contest_new " +
                   "set judge_mode = :judgeMode ",
           nativeQuery = true
    )
    void switchAllContestToJudgeMode(String judgeMode);

    List<ContestEntity> findByContestPublicTrue();
}
