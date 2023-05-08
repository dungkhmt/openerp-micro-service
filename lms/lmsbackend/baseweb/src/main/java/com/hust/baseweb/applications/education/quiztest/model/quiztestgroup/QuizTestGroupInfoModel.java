package com.hust.baseweb.applications.education.quiztest.model.quiztestgroup;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizTestGroupInfoModel {

    String quizGroupId;

    String groupCode;

    String note;

    int numQuestion;

    int numStudent;
}
