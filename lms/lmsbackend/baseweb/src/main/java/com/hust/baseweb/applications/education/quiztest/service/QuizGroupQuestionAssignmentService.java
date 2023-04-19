package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.quiztest.entity.QuizGroupQuestionAssignment;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.AddQuizGroupQuestionInputModel;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.QuizGroupQuestionDetailOutputModel;
import com.hust.baseweb.applications.education.quiztest.model.quitestgroupquestion.RemoveQuizGroupQuestionInputModel;

import java.util.List;

public interface QuizGroupQuestionAssignmentService {

    public List<QuizGroupQuestionDetailOutputModel> findAllQuizGroupQuestionAssignmentOfTest(String testId);

    public boolean removeQuizGroupQuestionAssignment(RemoveQuizGroupQuestionInputModel input);

    public QuizGroupQuestionAssignment addQuizGroupQuestionAssignment(AddQuizGroupQuestionInputModel input);
}
