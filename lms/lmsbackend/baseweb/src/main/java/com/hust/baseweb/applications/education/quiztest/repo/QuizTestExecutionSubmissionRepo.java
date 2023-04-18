package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.QuizTestExecutionSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface QuizTestExecutionSubmissionRepo extends JpaRepository<QuizTestExecutionSubmission, UUID> {
    /*
    @Query(value= "select * from quiz_group_question_assignment " +
                  "where quiz_group_id = ?1 order by seq asc", nativeQuery=true)
*/
    List<QuizTestExecutionSubmission> findAllByQuestionIdAndQuizGroupIdAndParticipationUserLoginIdOrderByCreatedStampDesc(UUID questionId, UUID quizGroupId, String participationUserLoginId);


}

