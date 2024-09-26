package com.hust.baseweb.applications.education.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.entity.QuizQuestionTag;
import com.hust.baseweb.applications.education.entity.QuizTag;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.repo.QuizQuestionTagRepo;
import com.hust.baseweb.applications.education.repo.QuizTagRepo;

import java.util.*;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class QuizQuestionTagServiceImpl implements QuizQuestionTagService{
    private QuizQuestionTagRepo quizQuestionTagRepo;
    private QuizTagRepo quizTagRepo;
    private QuizQuestionService quizQuestionService;
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
    @Override
    public List<QuizQuestionDetailModel> getListQuizQuestionByTagsAndCourseId(List<String> tags, String courseId){
        List<UUID> tagIds = quizTagRepo.findAllTagIdByCourseIdAndTagName(courseId, tags);
        List<UUID> questionIds = this.getListQuizQuestionByTagIds(tagIds);
        
        List<QuizQuestion> quizQuestions = quizQuestionService.findAllQuizQuestionsByQuestionIdsIn(questionIds);
        //List<QuizQuestion> quizQuestions = quizQuestionService.findAll();
        List<QuizQuestionDetailModel> quizQuestionDetailModels = new ArrayList<>();
        for (QuizQuestion quizQuestion : quizQuestions) {
            QuizQuestionDetailModel quizQuestionDetailModel = quizQuestionService.findQuizDetail(quizQuestion.getQuestionId());
            quizQuestionDetailModels.add(quizQuestionDetailModel);
        }
        Collections.sort(quizQuestionDetailModels, new Comparator<QuizQuestionDetailModel>() {
            @Override
            public int compare(QuizQuestionDetailModel o1, QuizQuestionDetailModel o2) {
                String topic1 = o1.getQuizCourseTopic().getQuizCourseTopicId();
                String topic2 = o2.getQuizCourseTopic().getQuizCourseTopicId();
                String level1 = o1.getLevelId();
                String level2 = o2.getLevelId();
                int c1 = topic1.compareTo(topic2);
                if (c1 == 0) {
                    return level1.compareTo(level2);
                } else {
                    return c1;
                }
            }
        });
        return quizQuestionDetailModels;
    }
}
