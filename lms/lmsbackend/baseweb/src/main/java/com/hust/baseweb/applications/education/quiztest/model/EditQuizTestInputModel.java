package com.hust.baseweb.applications.education.quiztest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditQuizTestInputModel {

    private String testId;
    private Date scheduleDate;
    private int duration;
    private String statusId;
    private String questionStatementViewTypeId;
    private String viewTypeId;
    private String participantQuizGroupAssignmentMode;
    private String judgeMode;
}
