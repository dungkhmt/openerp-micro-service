package com.hust.baseweb.applications.admin.dataadmin.education.service;

import com.hust.baseweb.applications.admin.dataadmin.education.model.ProgrammingContestSubmissionOM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProgrammingContestSubmissionService {

    Page<ProgrammingContestSubmissionOM> findContestSubmissionsOfStudent(
        String studentLoginId,
        String search,
        Pageable pageable
    );

}
