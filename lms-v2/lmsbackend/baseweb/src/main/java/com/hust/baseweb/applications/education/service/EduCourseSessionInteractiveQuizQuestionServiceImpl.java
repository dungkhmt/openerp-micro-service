package com.hust.baseweb.applications.education.service;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.entity.EduCourseSessionInteractiveQuizQuestion;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizQuestion;
import com.hust.baseweb.applications.education.repo.EduCourseSessionInteractiveQuizQuestionRepo;
import com.hust.baseweb.applications.education.repo.QuizQuestionRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EduCourseSessionInteractiveQuizQuestionServiceImpl implements EduCourseSessionInteractiveQuizQuestionService{
    private EduCourseSessionInteractiveQuizQuestionRepo eduCourseSessionInteractiveQuizQuestionRepo;
    private QuizQuestionRepo quizQuestionRepo;
    private QuizQuestionService quizQuestionService;

    @Override
    public List<QuizQuestionDetailModel> findAllByInteractiveQuizId(UUID interactiveQuizId){
        List<EduCourseSessionInteractiveQuizQuestion> eduCourseSessionInteractiveQuizQuestions = eduCourseSessionInteractiveQuizQuestionRepo.findByInteractiveQuizId(interactiveQuizId);
            // .findAllByTestIdAndStatusId(testId, EduQuizTestQuizQuestion.STATUS_CREATED);


        List<UUID> questionIds = new ArrayList();
        for (EduCourseSessionInteractiveQuizQuestion q : eduCourseSessionInteractiveQuizQuestions) {
            questionIds.add(q.getQuestionId());
        }
        List<QuizQuestion> quizQuestions = quizQuestionRepo.findAllByQuestionIdIn(questionIds);

        List<QuizQuestionDetailModel> quizQuestionDetailModels = new ArrayList<>();
        for (QuizQuestion q : quizQuestions) {
            if (q.getStatusId().equals(QuizQuestion.STATUS_PUBLIC)) {
                continue;
            }
            QuizQuestionDetailModel quizQuestionDetailModel = quizQuestionService.findQuizDetail(q.getQuestionId());
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
        // log.info("findAllByTestId, testId = " + testId
        //          + " RETURN list.sz = " + quizQuestionDetailModels.size());

        return quizQuestionDetailModels;
    
    }
}
