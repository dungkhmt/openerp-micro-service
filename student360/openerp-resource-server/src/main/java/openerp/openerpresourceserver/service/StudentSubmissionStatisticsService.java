package openerp.openerpresourceserver.service;

import openerp.openerpresourceserver.model.StudentStatisticContest;
import openerp.openerpresourceserver.entity.StudentSubmissionStatistics;

import java.util.List;

public interface StudentSubmissionStatisticsService {

    List<StudentSubmissionStatistics> getAllStatisticsDetailStudent();
    StudentStatisticContest getStaticsContestStudentId(String id);

}
