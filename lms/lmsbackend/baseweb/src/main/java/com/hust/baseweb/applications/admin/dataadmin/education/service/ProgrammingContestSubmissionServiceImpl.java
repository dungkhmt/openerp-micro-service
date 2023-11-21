package com.hust.baseweb.applications.admin.dataadmin.education.service;

import com.hust.baseweb.applications.admin.dataadmin.education.model.ProgrammingContestSubmissionOM;
import com.hust.baseweb.applications.programmingcontest.entity.ContestSubmissionEntity;
import com.hust.baseweb.applications.programmingcontest.model.ContestSubmission;
import com.hust.baseweb.applications.programmingcontest.repo.ContestSubmissionRepo;
import com.hust.baseweb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProgrammingContestSubmissionServiceImpl {

    private final ContestSubmissionRepo contestSubmissionRepo;

    private final UserService userService;

    public Page<ProgrammingContestSubmissionOM> findContestSubmissionsOfStudent(
        String studentLoginId,
        String search,
        Pageable pageable
    ) {
        return contestSubmissionRepo.findContestSubmissionsOfStudent(studentLoginId, search, pageable);
    }

    public Page<ContestSubmission> search(ContestSubmissionEntity filter, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                                               .withIgnorePaths(
                                                   "contestSubmissionId",
                                                   "testCasePass",
                                                   "sourceCode",
                                                   "runtime",
                                                   "memoryUsage",
                                                   "point",
                                                   "createdAt",
                                                   "updateAt",
                                                   "lastUpdatedByUserId")
                                               .withIgnoreNullValues()
                                               .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                                               .withIgnoreCase();
        Example<ContestSubmissionEntity> example = Example.of(filter, matcher);
        Page<ContestSubmissionEntity> submissions = contestSubmissionRepo.findAll(example, pageable);

        return submissions.map((submission) -> ContestSubmission
            .builder()
            .contestSubmissionId(submission.getContestSubmissionId())
            .problemId(submission.getProblemId())
            .contestId(submission.getContestId())
            .userId(submission.getUserId())
            .testCasePass(submission.getTestCasePass())
            .sourceCodeLanguage(submission.getSourceCodeLanguage())
//                .point(submission.getPoint())
            .status(submission.getStatus())
            .submissionDate(submission.getCreatedAt())
            .managementStatus(submission.getManagementStatus())
            .fullname(userService.getUserFullName(submission.getUserId()))
            .build());
    }
}
