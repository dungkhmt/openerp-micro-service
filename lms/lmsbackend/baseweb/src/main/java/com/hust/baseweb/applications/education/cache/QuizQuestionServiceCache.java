package com.hust.baseweb.applications.education.cache;

import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.programmingcontest.cache.RedisCacheService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class QuizQuestionServiceCache {

    private RedisCacheService cacheService;

    public void flushCache() {
        cacheService.flushCache(QuizQuestionDetailModel.class.getName());
    }

    public void addInteractiveQuizQuestionToCache(String interactiveQuizId, List<QuizQuestionDetailModel> listQuestions) {
        String key = interactiveQuizId;
        Date now = new Date();
        cacheService.pushCachedWithExpire(
            QuizQuestionDetailModel.class.getName(),
            key,
            listQuestions,
            1 * 60 * 60 * 1000);
    }

    public List<QuizQuestionDetailModel> findInteractiveQuizQuestionInCache(String interactiveQuizId) {
        return cacheService.getCachedObject(QuizQuestionDetailModel.class.getName(), interactiveQuizId, List.class);
    }

}
