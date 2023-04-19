package com.hust.baseweb.applications.education.repo;

import com.hust.baseweb.applications.admin.dataadmin.education.model.DoingPracticeQuizLogsOM;
import com.hust.baseweb.applications.education.entity.LogUserLoginQuizQuestion;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface LogUserLoginQuizQuestionRepo extends JpaRepository<LogUserLoginQuizQuestion, UUID> {

    LogUserLoginQuizQuestion save(LogUserLoginQuizQuestion logUserLoginQuizQuestion);

    Page<LogUserLoginQuizQuestion> findByUserLoginId(String userLoginId, Pageable page);

    @Query(
        nativeQuery = true,
        value = "SELECT course.id courseId, course.course_name courseName, cls.code classCode, cls.semester_id semester, " +
                    "CAST(lg.question_topic_id as VARCHAR(60)) topicId, lg.question_topic_name topicName, " +
                    "CAST(lg.question_id as VARCHAR(60)) questionId, lg.created_stamp doAt, " +
                    "CASE " +
                        "WHEN lg.is_correct_answer = 'Y' THEN 1 " +
                        "WHEN lg.is_correct_answer = 'N' THEN 0 " +
                    "END grade " +
                "FROM (SELECT * FROM log_user_login_quiz_question WHERE user_login_id = :studentLoginId) lg " +
                "LEFT JOIN edu_class cls " +
                    "ON lg.class_id = cls.id " +
                "LEFT JOIN edu_course course " +
                    "ON cls.course_id = course.id " +
                "WHERE LOWER(course.id) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(course.course_name) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(cls.code AS VARCHAR(6))) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(cls.semester_id AS CHAR(5))) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(lg.question_topic_name) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(lg.question_id AS VARCHAR(60))) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR LOWER(CAST(lg.created_stamp AS VARCHAR(60))) LIKE CONCAT('%', LOWER(:search), '%') " +
                    "OR ( (:search = '1' AND lg.is_correct_answer = 'Y') OR (:search = '0' AND lg.is_correct_answer = 'N') ) " +
                "ORDER BY lg.created_stamp DESC",
        countQuery = "SELECT COUNT(*)" +
                     "FROM (SELECT * FROM log_user_login_quiz_question WHERE user_login_id = :studentLoginId) lg " +
                     "LEFT JOIN edu_class cls " +
                        "ON lg.class_id = cls.id " +
                     "LEFT JOIN edu_course course " +
                        "ON cls.course_id = course.id " +
                     "WHERE LOWER(course.id) LIKE CONCAT('%', LOWER(:search), '%') " +
                        "OR LOWER(course.course_name) LIKE CONCAT('%', LOWER(:search), '%') " +
                        "OR LOWER(CAST(cls.code AS VARCHAR(6))) LIKE CONCAT('%', LOWER(:search), '%') " +
                        "OR LOWER(CAST(cls.semester_id AS CHAR(5))) LIKE CONCAT('%', LOWER(:search), '%') " +
                        "OR LOWER(lg.question_topic_name) LIKE CONCAT('%', LOWER(:search), '%') " +
                        "OR LOWER(CAST(lg.question_id AS VARCHAR(60))) LIKE CONCAT('%', LOWER(:search), '%') " +
                        "OR LOWER(CAST(lg.created_stamp AS VARCHAR(60))) LIKE CONCAT('%', LOWER(:search), '%') " +
                        "OR ( (:search = '1' AND lg.is_correct_answer = 'Y') OR (:search = '0' AND lg.is_correct_answer = 'N') ) "

    )
    Page<DoingPracticeQuizLogsOM> findDoingPracticeQuizLogsOfStudent(@Param("studentLoginId") String studentLoginId,
                                                                     @Param("search") String search,
                                                                     Pageable pageable);
}
