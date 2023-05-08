package com.hust.baseweb.applications.education.quiztest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserQuestionQuizExecutionOM {
    private String userLoginId;
    private String fullName;
    private UUID questionId;
    private UUID quizGroupId;
    private String testId;
    private String courseId;
    private String classId;
    private UUID sessionId;
    private String sessionName;
    private int grade;
    private Date date;
}
