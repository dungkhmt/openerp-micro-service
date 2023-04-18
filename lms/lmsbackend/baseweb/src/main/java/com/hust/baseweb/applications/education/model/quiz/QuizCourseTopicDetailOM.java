package com.hust.baseweb.applications.education.model.quiz;


import com.hust.baseweb.applications.education.entity.QuizQuestion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizCourseTopicDetailOM {
    private String quizCourseTopicId;
    private String quizCourseTopicName;
    private int numberOfPublishedQuizs;
    private int numberOfPrivateQuizs;
    private List<QuizQuestion> quizQuestions;
}
