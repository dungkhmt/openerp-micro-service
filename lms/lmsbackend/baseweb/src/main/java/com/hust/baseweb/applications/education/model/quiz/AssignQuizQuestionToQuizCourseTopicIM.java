package com.hust.baseweb.applications.education.model.quiz;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssignQuizQuestionToQuizCourseTopicIM {
    private UUID questionId;
    private String quiCourseTopicId;

}
