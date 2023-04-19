package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.QuizCourseTopic;
import com.hust.baseweb.applications.education.model.quiz.QuizCourseTopicCreateInputModel;
import com.hust.baseweb.applications.education.model.quiz.QuizCourseTopicDetailOM;

import java.util.List;

public interface QuizCourseTopicService {

    public List<QuizCourseTopic> findAll();

    public List<QuizCourseTopic> findByEduCourse_Id(String courseId);

    public List<QuizCourseTopicDetailOM> findTopicByCourseId(String courseId);

    public QuizCourseTopic save(QuizCourseTopicCreateInputModel input);

    public List<QuizCourseTopic> findAllByEduCourse(String courseId);
}
