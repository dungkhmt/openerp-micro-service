package com.hust.baseweb.applications.education.service;

import com.hust.baseweb.applications.education.entity.QuizCourseTopic;
import com.hust.baseweb.applications.education.model.quiz.QuizCourseTopicCreateInputModel;
import com.hust.baseweb.applications.education.model.quiz.QuizCourseTopicDetailOM;

import java.util.List;

public interface QuizCourseTopicService {

    List<QuizCourseTopic> findAll();

    List<QuizCourseTopic> findByEduCourse_Id(String courseId);

    List<QuizCourseTopicDetailOM> findTopicByCourseId(String courseId);

    QuizCourseTopic save(QuizCourseTopicCreateInputModel input);

    List<QuizCourseTopic> findAllByEduCourse(String courseId);
}
