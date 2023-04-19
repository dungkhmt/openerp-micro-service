package com.hust.baseweb.applications.admin.dataadmin.education.service;

import com.hust.baseweb.applications.admin.dataadmin.education.model.ProgrammingContestSubmissionOM;
import com.hust.baseweb.applications.programmingcontest.repo.ContestSubmissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ProgrammingContestSubmissionServiceImpl implements ProgrammingContestSubmissionService {

    private final ContestSubmissionRepo contestSubmissionRepo;

    @Override
    public Page<ProgrammingContestSubmissionOM> findContestSubmissionsOfStudent(
        String studentLoginId,
        String search,
        Pageable pageable
    ) {
        return contestSubmissionRepo.findContestSubmissionsOfStudent(studentLoginId, search, pageable);
    }
}
