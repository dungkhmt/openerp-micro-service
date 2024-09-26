package com.hust.baseweb.applications.education.quiztest.entity;

import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeQuizGroupQuestionParticipationExecutionId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "quiz_group_question_participation_execution")
@IdClass(CompositeQuizGroupQuestionParticipationExecutionId.class)

public class QuizGroupQuestionParticipationExecution {

    @Id
    @Column(name = "question_id")
    private UUID questionId;

    @Id
    @Column(name = "quiz_group_id")
    private UUID quizGroupId;

    @Id
    @Column(name = "participation_user_login_id")
    private String participationUserLoginId;

}
