package com.hust.baseweb.applications.admin.dataadmin.education.service;

import com.hust.baseweb.applications.admin.dataadmin.education.model.DoingPracticeQuizLogsOM;
import com.hust.baseweb.applications.education.repo.LogUserLoginQuizQuestionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DoingPracticeQuizLogsServiceImpl implements DoingPracticeQuizLogsService {

    private final LogUserLoginQuizQuestionRepo doingPracticeQuizLogRepo;

    @Override
    public Page<DoingPracticeQuizLogsOM> findDoingPracticeQuizLogsOfStudent(
        String studentLoginId,
        String search,
        Pageable pageable
    ) {
        return doingPracticeQuizLogRepo.findDoingPracticeQuizLogsOfStudent(studentLoginId, search, pageable);
    }

}
