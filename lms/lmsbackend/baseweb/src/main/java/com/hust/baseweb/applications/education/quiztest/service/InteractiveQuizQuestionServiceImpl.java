package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.programmingcontest.callexternalapi.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;

import com.hust.baseweb.applications.education.cache.QuizQuestionServiceCache;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTestQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuiz;
import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeInteractiveQuizQuestionId;
import com.hust.baseweb.applications.education.quiztest.model.InteractiveQuizQuestionInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizQuestionRepo;
import com.hust.baseweb.applications.education.quiztest.repo.InteractiveQuizRepo;
import com.hust.baseweb.applications.education.repo.QuizQuestionRepo;
import com.hust.baseweb.applications.education.service.QuizQuestionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class InteractiveQuizQuestionServiceImpl implements InteractiveQuizQuestionService {
    private InteractiveQuizQuestionRepo interactiveQuizQuestionRepo;
    private InteractiveQuizRepo interactiveQuizRepo;
    private QuizQuestionRepo quizQuestionRepo;
    private QuizQuestionService quizQuestionService;
    private QuizQuestionServiceCache cacheService;
    private ApiService apiService;


    @Override
    public List<QuizQuestionDetailModel> findAllByInteractiveQuizId(UUID interactiveQuizId){
        //List<EduQuizTestQuizQuestion> eduQuizTestQuizQuestions = eduQuizTestQuizQuestionRepo.findAllByTestId(testId);
        // InteractiveQuiz interactiveQuiz = interactiveQuizRepo.findById(interactiveQuizId).orElse(null);
        // if (interactiveQuiz == null || !interactiveQuiz.getStatusId().equals("OPENED")) {
        //     return new ArrayList<>();
        // }

        /*
            Has Bug that cannot load object from cache, to be fixed and recover later
        List<QuizQuestionDetailModel> interactiveQuizQuestionsCache = cacheService.findInteractiveQuizQuestionInCache(interactiveQuizId.toString());
        if(interactiveQuizQuestionsCache != null){
            return interactiveQuizQuestionsCache;
        }
         */

        List<InteractiveQuizQuestion> interactiveQuizQuestions = interactiveQuizQuestionRepo.findAllByInteractiveQuizId(interactiveQuizId);
            // .findAllByTestIdAndStatusId(testId, EduQuizTestQuizQuestion.STATUS_CREATED);


        List<UUID> questionIds = new ArrayList();
        for (InteractiveQuizQuestion q : interactiveQuizQuestions) {
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

        cacheService.addInteractiveQuizQuestionToCache(interactiveQuizId.toString(), quizQuestionDetailModels);

        return quizQuestionDetailModels;
    };

    public void removeFromInteractiveQuiz(InteractiveQuizQuestionInputModel input){
        CompositeInteractiveQuizQuestionId id = new CompositeInteractiveQuizQuestionId();
        id.setInteractiveQuizId(input.getInteractiveQuizId());
        id.setQuestionId(input.getQuestionId());
        interactiveQuizQuestionRepo.deleteById(id);
    }
}
