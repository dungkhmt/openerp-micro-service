package com.hust.baseweb.applications.exam.model.response;

public interface ExamTestQuestionDetailsRes {

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
    String getQuestionAnswer();
    String getQuestionExplain();
    Integer getQuestionOrder();
}
