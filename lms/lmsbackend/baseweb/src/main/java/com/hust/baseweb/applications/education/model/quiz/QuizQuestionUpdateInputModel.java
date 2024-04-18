package com.hust.baseweb.applications.education.model.quiz;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizQuestionUpdateInputModel {

    private String quizCourseTopicId;

    private String levelId;

    private String questionContent;

    private String[] fileId;

    private String solutionContent;

    private String[] deletedAttachmentIds;

    private List<String> chooseTags;

    private String courseId;
}
