package com.hust.baseweb.applications.education.service;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.entity.QuizQuestionTag;
import com.hust.baseweb.applications.education.entity.QuizTag;
import com.hust.baseweb.applications.education.repo.QuizQuestionTagRepo;
import com.hust.baseweb.applications.education.repo.QuizTagRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class QuizQuestionTagServiceImpl implements QuizQuestionTagService{
    private QuizQuestionTagRepo quizQuestionTagRepo;
    @Override
    public QuizQuestionTag createQuizQuestionTag(UUID questionId, UUID tagId){
        QuizQuestionTag quizQuestionTag = new QuizQuestionTag();
        quizQuestionTag.setQuestionId(questionId);
        quizQuestionTag.setTagId(tagId);
        quizQuestionTag.setCreatedStamp(new Date());
        quizQuestionTag.setLastUpdated(new Date());
        return quizQuestionTagRepo.save(quizQuestionTag);
    }

    @Override
    public List<UUID> getListQuizQuestionByTagIds(List<UUID> tagIds){
        List<UUID> questionIds = quizQuestionTagRepo.findByTagIdIn(tagIds, tagIds.size());
        // List<UUID> questionIds = new ArrayList<>();
        // for (QuizQuestionTag quizQuestionTag : quizQuestionTags) {
        //     questionIds.add(quizQuestionTag.getQuestionId());
        // }
        return questionIds;
    }
}
