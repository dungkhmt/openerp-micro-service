package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.EduTestQuizGroupParticipationAssignment;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeEduTestQuizGroupParticipationAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EduTestQuizGroupParticipationAssignmentRepo
    extends JpaRepository<EduTestQuizGroupParticipationAssignment, CompositeEduTestQuizGroupParticipationAssignmentId> {

    List<EduTestQuizGroupParticipationAssignment> findEduTestQuizGroupParticipationAssignmentsByParticipationUserLoginId(
        String userId
    );

    List<EduTestQuizGroupParticipationAssignment> findAllByQuizGroupIdIn(List<UUID> quizGroupIds);

    List<EduTestQuizGroupParticipationAssignment> findAllByQuizGroupIdInAndParticipationUserLoginId(
        List<UUID> quizGroupIds,
        String participationUserLoginId
    );

    EduTestQuizGroupParticipationAssignment findByQuizGroupIdAndParticipationUserLoginId(
        UUID quizGroupId,
        String participationUserLoginId
    );
    List<EduTestQuizGroupParticipationAssignment> findAllByQuizGroupIdAndParticipationUserLoginId(
        UUID quizGroupId,
        String participationUserLoginId
    );

    @Override
    boolean existsById(CompositeEduTestQuizGroupParticipationAssignmentId compositeEduTestQuizGroupParticipationAssignmentId);

    EduTestQuizGroupParticipationAssignment save(EduTestQuizGroupParticipationAssignment eduTestQuizGroupParticipationAssignment);

}
