package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.EduCourse;
import com.hust.baseweb.applications.education.entity.QuizCourseTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizCourseTopicRepo extends JpaRepository<QuizCourseTopic, String> {

    public List<QuizCourseTopic> findByEduCourse_Id(String courseId);

    public List<QuizCourseTopic> findAllByEduCourse(EduCourse eduCourse);
}
