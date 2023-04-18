package com.hust.baseweb.applications.education.quiztest.service;

import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.model.quiz.QuizQuestionDetailModel;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTest;
import com.hust.baseweb.applications.education.quiztest.entity.EduQuizTestQuizQuestion;
import com.hust.baseweb.applications.education.quiztest.model.EduQuizTestModel;
import com.hust.baseweb.applications.education.quiztest.model.quiztestquestion.CreateQuizTestQuestionInputModel;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestQuizQuestionRepo;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestRepo;
import com.hust.baseweb.applications.education.repo.QuizQuestionRepo;
import com.hust.baseweb.applications.education.service.QuizQuestionService;
import com.hust.baseweb.entity.UserLogin;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class EduQuizTestQuizQuestionServiceImpl implements EduQuizTestQuizQuestionService{
    private EduQuizTestQuizQuestionRepo eduQuizTestQuizQuestionRepo;
    private QuizQuestionRepo quizQuestionRepo;
    private QuizQuestionService quizQuestionService;
    private EduQuizTestRepo eduQuizTestRepo;
    @Override
    public EduQuizTestQuizQuestion createQuizTestQuestion(UserLogin u, CreateQuizTestQuestionInputModel input) {
        EduQuizTestQuizQuestion eduQuizTestQuizQuestion = eduQuizTestQuizQuestionRepo.findByTestIdAndQuestionId(input.getTestId(), input.getQuestionId());
        if(eduQuizTestQuizQuestion != null){
            log.info("createQuizTestQuestion, item (test " + input.getTestId() + ", question " + input.getQuestionId() + ") EXISTS");
            if(!eduQuizTestQuizQuestion.getStatusId().equals(EduQuizTestQuizQuestion.STATUS_CREATED)){
                eduQuizTestQuizQuestion.setStatusId(EduQuizTestQuizQuestion.STATUS_CREATED);
                eduQuizTestQuizQuestion = eduQuizTestQuizQuestionRepo.save(eduQuizTestQuizQuestion);
            }
            return eduQuizTestQuizQuestion;
        }
        eduQuizTestQuizQuestion = new EduQuizTestQuizQuestion();
        eduQuizTestQuizQuestion.setQuestionId(input.getQuestionId());
        eduQuizTestQuizQuestion.setTestId(input.getTestId());
        eduQuizTestQuizQuestion.setCreatedByUserLoginId(u.getUserLoginId());
        eduQuizTestQuizQuestion.setCreatedStamp(new Date());
        eduQuizTestQuizQuestion.setStatusId(EduQuizTestQuizQuestion.STATUS_CREATED);

        eduQuizTestQuizQuestion = eduQuizTestQuizQuestionRepo.save(eduQuizTestQuizQuestion);

        return eduQuizTestQuizQuestion;
    }

    @Override
    public int createQuizTestQuestion(UserLogin u, String testId, UUID questionId) {
        EduQuizTestQuizQuestion eduQuizTestQuizQuestion = eduQuizTestQuizQuestionRepo
            .findByTestIdAndQuestionId(testId, questionId);
        if(eduQuizTestQuizQuestion != null){
            log.info("createQuizTestQuestion, item (test " + testId + ", question " + questionId + ") EXISTS");
            if(!eduQuizTestQuizQuestion.getStatusId().equals(EduQuizTestQuizQuestion.STATUS_CREATED)){
                eduQuizTestQuizQuestion.setStatusId(EduQuizTestQuizQuestion.STATUS_CREATED);
                eduQuizTestQuizQuestion = eduQuizTestQuizQuestionRepo.save(eduQuizTestQuizQuestion);
            }
            return 0;
        }
        eduQuizTestQuizQuestion = new EduQuizTestQuizQuestion();
        eduQuizTestQuizQuestion.setQuestionId(questionId);
        eduQuizTestQuizQuestion.setTestId(testId);
        eduQuizTestQuizQuestion.setCreatedByUserLoginId(u.getUserLoginId());
        eduQuizTestQuizQuestion.setCreatedStamp(new Date());
        eduQuizTestQuizQuestion.setStatusId(EduQuizTestQuizQuestion.STATUS_CREATED);

        eduQuizTestQuizQuestion = eduQuizTestQuizQuestionRepo.save(eduQuizTestQuizQuestion);

        return 1;
    }

    @Override
    public EduQuizTestQuizQuestion removeQuizTestQuestion(UserLogin u, CreateQuizTestQuestionInputModel input) {
        EduQuizTestQuizQuestion eduQuizTestQuizQuestion = eduQuizTestQuizQuestionRepo
            .findByTestIdAndQuestionId(input.getTestId(),input.getQuestionId());

        if(eduQuizTestQuizQuestion != null){
            eduQuizTestQuizQuestion.setStatusId(EduQuizTestQuizQuestion.STATUS_CANCELLED);
            eduQuizTestQuizQuestion = eduQuizTestQuizQuestionRepo.save(eduQuizTestQuizQuestion);
        }
        return eduQuizTestQuizQuestion;
    }

    @Override
    public List<QuizQuestionDetailModel> findAllByTestId(String testId) {
        //List<EduQuizTestQuizQuestion> eduQuizTestQuizQuestions = eduQuizTestQuizQuestionRepo.findAllByTestId(testId);
        List<EduQuizTestQuizQuestion> eduQuizTestQuizQuestions = eduQuizTestQuizQuestionRepo
            .findAllByTestIdAndStatusId(testId, EduQuizTestQuizQuestion.STATUS_CREATED);


        List<UUID> questionIds = new ArrayList();
        for(EduQuizTestQuizQuestion q: eduQuizTestQuizQuestions){
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
        log.info("findAllByTestId, testId = " + testId
                 + " RETURN list.sz = " + quizQuestionDetailModels.size());

        return quizQuestionDetailModels;

    }

    @Override
    public List<EduQuizTestModel> getQuizTestsUsingQuestion(UUID questionId) {
        List<EduQuizTestQuizQuestion> quizTestQuestions = eduQuizTestQuizQuestionRepo.findAllByQuestionId(questionId);
        List<EduQuizTestModel> eduQuizTestModels = new ArrayList();
        for(EduQuizTestQuizQuestion qq: quizTestQuestions){
            EduQuizTest quizTest = eduQuizTestRepo.findById(qq.getTestId()).orElse(null);
            if(quizTest != null){
                EduQuizTestModel q = new EduQuizTestModel();
                q.setTestId(quizTest.getTestId());
                q.setStatusId(quizTest.getStatusId());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                String strDate = dateFormat.format(quizTest.getScheduleDatetime());
                q.setScheduleDatetime(strDate);
                q.setTestName(quizTest.getTestName());
                eduQuizTestModels.add(q);
            }
        }
        return eduQuizTestModels;
    }
}
