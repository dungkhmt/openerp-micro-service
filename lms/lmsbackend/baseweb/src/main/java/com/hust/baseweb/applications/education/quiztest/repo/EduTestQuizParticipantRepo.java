package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizParticipant;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeTestQuizParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EduTestQuizParticipantRepo
    extends JpaRepository<EduTestQuizParticipant, CompositeTestQuizParticipationId> {

    List<EduTestQuizParticipant> findByTestIdAndParticipantUserLoginId(String testId, String participantUserLoginId);

    List<EduTestQuizParticipant> findByParticipantUserLoginIdAndStatusId(String participantUserLoginId, String statusId);

    List<EduTestQuizParticipant> findByTestIdAndStatusId(String testId, String statusId);

    EduTestQuizParticipant findEduTestQuizParticipantByParticipantUserLoginIdAndAndTestId(String userId, String testId);
}
