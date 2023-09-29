package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.composite.CompositeContestUserParticipantGroupId;
import com.hust.baseweb.applications.programmingcontest.entity.ContestUserParticipantGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public interface ContestUserParticipantGroupRepo extends
    JpaRepository<ContestUserParticipantGroup, CompositeContestUserParticipantGroupId> {
    ContestUserParticipantGroup findByContestIdAndUserIdAndParticipantId(String contestId, String userId, String participantId);
}
