package com.hust.baseweb.applications.education.report.service;

import com.hust.baseweb.applications.education.report.model.GetClassParticipationStatisticInputModel;
import com.hust.baseweb.applications.education.report.model.StudentClassParticipationOutputModel;

import java.util.List;

public interface StudentClassParticipationService {

    public List<StudentClassParticipationOutputModel> getStudentClassParticipationStatistic(
        GetClassParticipationStatisticInputModel input
    );
}
