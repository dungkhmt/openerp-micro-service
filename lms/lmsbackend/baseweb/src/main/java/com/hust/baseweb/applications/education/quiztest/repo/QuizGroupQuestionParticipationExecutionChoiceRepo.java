package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.QuizGroupQuestionParticipationExecutionChoice;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeQuizGroupQuestionParticipationExecutionChoiceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizGroupQuestionParticipationExecutionChoiceRepo
    extends JpaRepository<QuizGroupQuestionParticipationExecutionChoice, CompositeQuizGroupQuestionParticipationExecutionChoiceId> {

    List<QuizGroupQuestionParticipationExecutionChoice> findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupId(
        String userId,
        UUID groupId
    );
    List<QuizGroupQuestionParticipationExecutionChoice> findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginId(
        String userId
    );

    List<QuizGroupQuestionParticipationExecutionChoice> findQuizGroupQuestionParticipationExecutionChoicesByParticipationUserLoginIdAndQuizGroupIdAndQuestionId(
        String userId,
        UUID quizgroupId,
        UUID questionId
    );

    List<QuizGroupQuestionParticipationExecutionChoice> findAllByParticipationUserLoginIdInAndQuizGroupIdIn(
        List<String> userLoginIds, List<UUID> quizGroupIds);

        List<QuizGroupQuestionParticipationExecutionChoice> findByQuizGroupIdIn(List<UUID> quizGroupIds);

    List<QuizGroupQuestionParticipationExecutionChoice> findByQuizGroupId(UUID quizGroupId);

}
