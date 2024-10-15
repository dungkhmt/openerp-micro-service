package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionComment; // Ensure this is correct
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContestSubmissionCommentRepository extends JpaRepository<ContestSubmissionComment, UUID> {
    // You can define additional query methods here if needed
    List<ContestSubmissionComment> findBySubmissionId(UUID submissionId);

    List<ContestSubmissionComment> findBySubmissionIdOrderByCreatedStampDesc(UUID submissionId);
}
