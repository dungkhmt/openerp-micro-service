package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTestQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.model.EduQuizTestModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestquestion.CreateQuizTestQuestionInputModel;
import com.hust.baseweb.entity.UserLogin;

import java.util.List;
import java.util.UUID;

public interface EduQuizTestQuizQuestionService {
    public EduQuizTestQuizQuestion createQuizTestQuestion(UserLogin u, CreateQuizTestQuestionInputModel input);
    public int createQuizTestQuestion(UserLogin u, String testId, UUID questionId);
    public EduQuizTestQuizQuestion removeQuizTestQuestion(UserLogin u, CreateQuizTestQuestionInputModel input);
    public List<QuizQuestionDetailModel> findAllByTestId(String testId);

    public List<EduQuizTestModel> getQuizTestsUsingQuestion(UUID questionId);
}
