package com.hust.baseweb.applications.education.service;

import java.util.List;

import com.hust.baseweb.applications.education.entity.QuizTag;

public interface QuizTagService {
    public QuizTag createQuizTag(String courseId, String tagName);
}
