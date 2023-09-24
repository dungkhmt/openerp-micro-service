package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.AnalyzeParticipantDoingQuizInClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnalyzeParticipantDoingQuizInClassRepo extends JpaRepository<AnalyzeParticipantDoingQuizInClass, UUID> {
    List<AnalyzeParticipantDoingQuizInClass> findAllByClassId(UUID classId);
    List<AnalyzeParticipantDoingQuizInClass> findAllByClassIdAndParticipantUserloginId(UUID classId, String participantUserloginId);
}
