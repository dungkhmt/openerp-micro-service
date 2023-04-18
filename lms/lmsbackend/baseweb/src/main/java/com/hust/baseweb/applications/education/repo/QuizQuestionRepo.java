package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.education.entity.QuizCourseTopic;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionRepo extends JpaRepository<QuizQuestion, UUID> {

    public List<QuizQuestion> findAllByQuizCourseTopicIn(List<QuizCourseTopic> quizCourseTopics);
    public List<QuizQuestion> findAllByQuestionIdIn(List<UUID> questionIds);
    public List<QuizQuestion> findAllByQuizCourseTopic(QuizCourseTopic quizCourseTopic);
}
