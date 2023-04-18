package com.hust.baseweb.applications.admin.dataadmin.education.model;

import java.util.Date;

public interface DoingPracticeQuizLogsOM {
    String getCourseId();
    String getCourseName();
    String getClassCode();
    String getSemester();
    String getTopicId();
    String getTopicName();
    String getQuestionId();
    int getGrade();
    Date getDoAt();
}
