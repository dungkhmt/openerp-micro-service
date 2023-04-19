package com.hust.baseweb.applications.education.quiztest.model;


import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class QuizGroupTestDetailModel {
    private String participantUserId;

    private String testId;

    private String testName;

    private String scheduleDatetime;

    private String courseName;

    private Integer duration;

    private String quizGroupId;
    private String groupCode;

    private String viewTypeId;
    private String judgeMode;
    private List<QuizQuestionDetailModel> listQuestion;

    private Map<String, List<UUID>> participationExecutionChoice; //question id, list<choiceAnswerId>


}
