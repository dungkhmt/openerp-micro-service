package com.hust.baseweb.applications.education.quiztest.service;

import java.util.List;

import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizAnswer;

public interface InteractiveQuizAnswerService {
    public int submitAnswer(List<InteractiveQuizAnswer> interactiveQuizAnswer);
}
