package com.hust.baseweb.repo;


import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ContestSubmissionRepo extends JpaRepository<ContestSubmissionEntity, UUID> {

    ContestSubmissionEntity findByContestSubmissionId(UUID contestSubmissionId);
}
