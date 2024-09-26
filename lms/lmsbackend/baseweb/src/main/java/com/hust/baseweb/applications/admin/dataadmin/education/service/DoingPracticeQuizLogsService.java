package com.hust.baseweb.applications.admin.dataadmin.education.service;

import com.hust.baseweb.applications.admin.dataadmin.education.model.DoingPracticeQuizLogsOM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoingPracticeQuizLogsService {

    Page<DoingPracticeQuizLogsOM> findDoingPracticeQuizLogsOfStudent(
        String studentLoginId,
        String search,
        Pageable pageable
    );

}
