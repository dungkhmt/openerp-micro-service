package com.hust.baseweb.applications.programmingcontest.service;

import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import com.hust.baseweb.applications.programmingcontest.repo.ContestSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ContestSubmissionService {

    private final ContestSubmissionRepository contestSubmissionRepository;

    @Autowired
    public ContestSubmissionService(ContestSubmissionRepository contestSubmissionRepository) {
        this.contestSubmissionRepository = contestSubmissionRepository;
    }

    public ContestSubmissionEntity getSubmissionById(UUID submissionId) {
        Optional<ContestSubmissionEntity> submissionOptional = contestSubmissionRepository.findById(submissionId);

        if (submissionOptional.isPresent()) {
            return submissionOptional.get();
        } else {
            throw new RuntimeException("Submission not found with ID: " + submissionId);
        }
    }
}
