package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.composite.UserSubmissionContestResultID;
import com.hust.baseweb.applications.programmingcontest.entity.UserSubmissionContestResultNativeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserSubmissionContestResultNativeRepo extends JpaRepository<UserSubmissionContestResultNativeEntity, UserSubmissionContestResultID> {

    void deleteAllByContestId(String contestId);
}
