package com.hust.baseweb.applications.education.quiztest.repo;
import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hust.baseweb.applications.education.quiztest.entity.InteractiveQuiz;
import com.hust.baseweb.applications.education.quiztest.repo.EduQuizTestRepo.StudentInfo;


public interface InteractiveQuizRepo extends JpaRepository<InteractiveQuiz, UUID> {
    public List<InteractiveQuiz> findAllBySessionIdIn(Set<UUID> sessionIds);
    public List<InteractiveQuiz> findAllBySessionId(@Param("session_id") UUID sessionId);


    // public interface StudentResult {
    //     String getUserId();
    //     Integer getScore();
    // }

    public class StudentResult {
        private String userId;
        private Integer score;
    
        public StudentResult(String userId, Integer score) {
            this.userId = userId;
            this.score = score;
        }
    
        public String getUserId(){
            return userId;
        }
        public Integer getScore(){
            return score;
        }
        public void setUserId(String userId) {
            this.userId = userId;
        }
    
        public void setScore(Integer score) {
            this.score = score;
        }
    }

    public interface StudentSubmission {
        String getQuestionId();
        String getUserId();
        String getAnswerList();
        Boolean getIsCorrect();
        Date getCreatedStamp();
    }
    @Query(
        nativeQuery = true,
        value =
            "SELECT\r\n" + //
                    "    iqa.user_id as userId,\r\n" + //
                    "    SUM(case when correct_answers.correct_choices = total_choices.total_choices THEN 1 ELSE 0 END) as score\r\n" + //
                    "FROM\r\n" + //
                    "    interactive_quiz_answer iqa\r\n" + //
                    "JOIN\r\n" + //
                    "    quiz_choice_answer qca ON iqa.choice_answer_id = qca.choice_answer_id\r\n" + //
                    "JOIN\r\n" + //
                    "    quiz_question qq ON iqa.quiz_question_id = qq.question_id\r\n" + //
                    "LEFT JOIN\r\n" + //
                    "    (\r\n" + //
                    "        SELECT\r\n" + //
                    "            iqa.quiz_question_id,\r\n" + //
                    "            iqa.user_id,\r\n" + //
                    "            COUNT(*) AS correct_choices\r\n" + //
                    "        FROM\r\n" + //
                    "            interactive_quiz_answer iqa\r\n" + //
                    "        JOIN\r\n" + //
                    "            quiz_choice_answer qca ON iqa.choice_answer_id = qca.choice_answer_id\r\n" + //
                    "        WHERE\r\n" + //
                    "            qca.is_correct_answer = 'Y'\r\n" + //
                    "        AND iqa.interactive_quiz_id = ?1\r\n" + //
                    "        GROUP BY\r\n" + //
                    "            iqa.quiz_question_id, iqa.user_id\r\n" + //
                    "    ) AS correct_answers ON iqa.quiz_question_id = correct_answers.quiz_question_id\r\n" + //
                    "                           AND iqa.user_id = correct_answers.user_id\r\n" + //
                    "LEFT JOIN\r\n" + //
                    "    (\r\n" + //
                    "        SELECT\r\n" + //
                    "            iqa.quiz_question_id,\r\n" + //
                    "            iqa.user_id,\r\n" + //
                    "            COUNT(*) AS total_choices\r\n" + //
                    "        FROM\r\n" + //
                    "            interactive_quiz_answer iqa\r\n" + //
                    "        WHERE iqa.interactive_quiz_id = ?1\r\n" + //
                    "        GROUP BY\r\n" + //
                    "            iqa.quiz_question_id, iqa.user_id\r\n" + //
                    "    ) AS total_choices ON iqa.quiz_question_id = total_choices.quiz_question_id\r\n" + //
                    "                        AND iqa.user_id = total_choices.user_id\r\n" + //
                    "WHERE\r\n" + //
                    " iqa.interactive_quiz_id = ?1\r\n" + //
                    "GROUP BY\r\n" + //
                    "    iqa.user_id, iqa.interactive_quiz_id;"
    )

    public List<StudentResult> getResultOfInteractiveQuiz(UUID interactiveQuizId);

    @Query(
        nativeQuery = true, 
        value = "SELECT\r\n" + //
            "    Cast(qq.question_id as varchar) AS questionId,\r\n" + //
            "    iqa.user_id AS userId,\r\n" + //
            "    array_to_string(array_agg(qca.choice_answer_content),',') AS answerList,\r\n" + //
            "    CASE\r\n" + //
            "        WHEN SUM(CASE WHEN qca.is_correct_answer = 'Y' THEN 1 ELSE 0 END) = COUNT(*) THEN 'true'\r\n" + //
            "        ELSE 'false'\r\n" + //
            "    END AS isCorrect,\r\n" + //
            "    MAX(iqa.created_stamp) AS createdStamp\r\n" + //
            "FROM\r\n" + //
            "    interactive_quiz_answer iqa\r\n" + //
            "JOIN\r\n" + //
            "    quiz_choice_answer qca ON iqa.choice_answer_id = qca.choice_answer_id\r\n" + //
            "JOIN\r\n" + //
            "    quiz_question qq ON iqa.quiz_question_id = qq.question_id\r\n" + //
            "WHERE\r\n" + //
            "    iqa.interactive_quiz_id = ?1\r\n" + //
            "GROUP BY\r\n" + //
            "    qq.question_name, qq.question_id, iqa.user_id\r\n" + //
            "ORDER BY\r\n" + //
            "    qq.question_name, iqa.user_id")
    public List<StudentSubmission> getStudentSubmission(UUID interactiveQuizId);

}
