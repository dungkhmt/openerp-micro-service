package com.hust.baseweb.applications.exam.model.response;

public interface ExamStudentResultDetailsRes {

    String getId();
    String getCode();
    String getName();
    String getEmail();
    String getPhone();
    String getExamResultId();
    Integer getTotalScore();
    Integer getTotalTime();
    String getSubmitedAt();
}
