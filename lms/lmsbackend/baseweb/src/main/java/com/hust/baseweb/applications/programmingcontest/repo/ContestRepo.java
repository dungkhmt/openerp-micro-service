package com.hust.baseweb.applications.programmingcontest.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;

public interface ContestRepo extends JpaRepository<ContestEntity, String> {
    ContestEntity findContestByContestId(String contestId);

    ContestEntity findContestEntityByContestIdAndUserId(String contestId, String userId);

    @Modifying
    @Query(value = "update contest_new " +
                   "set judge_mode = :judgeMode " 
        ,
           nativeQuery = true
    )
    void switchAllContestToJudgeMode(String judgeMode);

}
