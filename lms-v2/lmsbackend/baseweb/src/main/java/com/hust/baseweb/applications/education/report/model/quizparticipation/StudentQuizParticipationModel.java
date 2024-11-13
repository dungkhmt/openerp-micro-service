package com.hust.baseweb.applications.education.report.model.quizparticipation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentQuizParticipationModel {

    private String userLoginId;

    private String fullName;

    private String affiliations;

    private String classId;

    private String classCode;

    private String questionId;

    private String courseId;

    private String courseName;

    private String topicName;

    private String topicId;

    private int grade;

    private Date createdStamp;
}
