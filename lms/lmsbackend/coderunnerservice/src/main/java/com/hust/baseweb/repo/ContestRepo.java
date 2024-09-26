package com.hust.baseweb.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ContestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepo extends JpaRepository<ContestEntity, String> {
    ContestEntity findContestByContestId(String contestId);

    ContestEntity findContestEntityByContestIdAndUserId(String contestId, String userId);

}
