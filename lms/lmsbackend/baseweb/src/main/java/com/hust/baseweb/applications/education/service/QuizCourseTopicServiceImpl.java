package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.cache.CacheQuizCourseTopic;
import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.entity.QuizCourseTopic;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.model.quiz.QuizCourseTopicCreateInputModel;
import com.hust.baseweb.applications.education.model.quiz.QuizCourseTopicDetailOM;
import com.hust.baseweb.applications.education.repo.EduCourseRepo;
import com.hust.baseweb.applications.education.repo.QuizCourseTopicRepo;
import com.hust.baseweb.applications.education.repo.QuizQuestionRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))

public class QuizCourseTopicServiceImpl implements QuizCourseTopicService {

    private QuizCourseTopicRepo quizCourseTopicRepo;
    private EduCourseRepo eduCourseRepo;
    private QuizQuestionRepo quizQuestionRepo;

    //public static HashMap<String, QuizCourseTopic> mId2QuizCourseTopic = null;
    public static CacheQuizCourseTopic cacheQuizCourseTopic  = null;
    @Override
    public List<QuizCourseTopic> findAll() {
        List<QuizCourseTopic> quizCourseTopics = quizCourseTopicRepo.findAll();
        if(cacheQuizCourseTopic == null){// for the first time
            //mId2QuizCourseTopic = new HashMap<String, QuizCourseTopic>();
            cacheQuizCourseTopic = new CacheQuizCourseTopic();
            for(QuizCourseTopic q: quizCourseTopics){
                //mId2QuizCourseTopic.put(q.getQuizCourseTopicId(),q);
                cacheQuizCourseTopic.put(q.getQuizCourseTopicId(),q);
            }
        }
        return quizCourseTopics;
    }

    public List<QuizCourseTopic> findByEduCourse_Id(String courseId) {
        return quizCourseTopicRepo.findByEduCourse_Id(courseId);
    }
    public List<QuizCourseTopicDetailOM> findTopicByCourseId(String courseId){
        List<QuizCourseTopic> lst = quizCourseTopicRepo.findByEduCourse_Id(courseId);
        List<QuizCourseTopicDetailOM> retList = new ArrayList();
        for(QuizCourseTopic qt: lst){
            List<QuizQuestion> quizs = quizQuestionRepo.findAllByQuizCourseTopic(qt);
            QuizCourseTopicDetailOM qd = new QuizCourseTopicDetailOM();
            qd.setQuizCourseTopicId(qt.getQuizCourseTopicId());
            qd.setQuizCourseTopicName(qt.getQuizCourseTopicName());
            qd.setQuizQuestions(quizs);
            int nbPublicQuizs = 0; int nbPrivateQuizs = 0;
            for(QuizQuestion q: quizs){
                if(q.getStatusId().equals(QuizQuestion.STATUS_PRIVATE)){
                    nbPrivateQuizs += 1;
                }else if(q.getStatusId().equals(QuizQuestion.STATUS_PUBLIC)){
                    nbPublicQuizs += 1;
                }
            }
            qd.setNumberOfPrivateQuizs(nbPrivateQuizs);
            qd.setNumberOfPublishedQuizs(nbPublicQuizs);
            retList.add(qd);
        }
        return retList;
    }
    @Override
    public QuizCourseTopic save(QuizCourseTopicCreateInputModel input) {
        QuizCourseTopic quizCourseTopic = new QuizCourseTopic();
        quizCourseTopic.setQuizCourseTopicId(input.getQuizCourseTopicId());
        quizCourseTopic.setQuizCourseTopicName(input.getQuizCourseTopicName());
        EduCourse eduCourse = eduCourseRepo.findById(input.getCourseId()).orElse(null);
        quizCourseTopic.setEduCourse(eduCourse);
        QuizCourseTopic quizCourseTopicDuplicate = quizCourseTopicRepo
            .findById(quizCourseTopic.getQuizCourseTopicId())
            .orElse(null);
        if (quizCourseTopicDuplicate == null) {
            quizCourseTopic = quizCourseTopicRepo.save(quizCourseTopic);
        } else {
            quizCourseTopic.setMessage("duplicate");
        }

        return quizCourseTopic;
    }

    @Override
    public List<QuizCourseTopic> findAllByEduCourse(String courseId) {
        EduCourse eduCourse = eduCourseRepo.findById(courseId).orElse(null);
        return quizCourseTopicRepo.findAllByEduCourse(eduCourse);
    }
}
