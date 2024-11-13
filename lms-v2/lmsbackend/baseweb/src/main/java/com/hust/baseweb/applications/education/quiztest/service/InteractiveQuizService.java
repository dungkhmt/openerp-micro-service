package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuiz;
import com.hust.baseweb.applications.education.quiztest.model.InteractiveQuizTestResultOutputModel;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo.StudentResult;

import java.util.List;
import java.util.UUID;

public interface InteractiveQuizService {
    public InteractiveQuiz createInteractiveQuiz(String interactiveName, UUID sessionId, String status);
    public InteractiveQuiz getInteractiveQuizById(String testId);
    public List<InteractiveQuiz> getAllInteractiveQuiz();
    public List<StudentResult> getQuizResults(UUID interactiveQuizId);
}
