package com.hust.baseweb.applications.education.service;

import java.util.List;
import java.util.UUID;

import com.hust.baseweb.applications.education.entity.QuizQuestionTag;

public interface QuizQuestionTagService {
    public QuizQuestionTag createQuizQuestionTag(UUID questionId, UUID tagId);
    public List<UUID> getListQuizQuestionByTagIds(List<UUID> tagIds);
}
