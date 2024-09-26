package com.hust.baseweb.applications.education.quiztest.repo;

import com.hust.baseweb.applications.education.quiztest.entity.HistoryLogQuizGroupQuestionParticipationExecutionChoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoryLogQuizGroupQuestionParticipationExecutionChoiceRepo
    extends JpaRepository<HistoryLogQuizGroupQuestionParticipationExecutionChoice, UUID> {

}
