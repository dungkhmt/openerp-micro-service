package com.hust.baseweb.applications.exam.model.response;

public interface MyExamQuestionDetailsRes {

    String getExamTestQuestionId();
    String getQuestionId();
    String getQuestionCode();
    Integer getQuestionType();
    String getQuestionContent();
    String getQuestionFile();
    Integer getQuestionNumberAnswer();
    String getQuestionContentAnswer1();
    String getQuestionContentAnswer2();
    String getQuestionContentAnswer3();
    String getQuestionContentAnswer4();
    String getQuestionContentAnswer5();
    boolean getQuestionMultichoice();
    Integer getQuestionOrder();
    Integer getTotalScore();
    Integer getTotalTime();
    String getSubmitedAt();
    String getAnswerFiles();
    String getAnswer();
    Integer getScore();
}
