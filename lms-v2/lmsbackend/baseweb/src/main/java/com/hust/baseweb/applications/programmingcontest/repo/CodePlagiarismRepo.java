package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.CodePlagiarism;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CodePlagiarismRepo extends JpaRepository<CodePlagiarism, UUID> {

    List<CodePlagiarism> findAllByContestId(String contestId);

    List<CodePlagiarism> findAllByContestIdAndProblemIdAndUserId1AndUserId2(String contestId, String problemId, String userId1, String userId2);

    List<CodePlagiarism> findAllByContestIdAndProblemIdAndSubmissionId1AndSubmissionId2(String contestId, String problemId, UUID submissionId1, UUID submissionId2);

}
