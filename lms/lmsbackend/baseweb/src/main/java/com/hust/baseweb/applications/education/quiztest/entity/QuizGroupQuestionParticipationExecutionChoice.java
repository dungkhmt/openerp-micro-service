package com.hust.baseweb.applications.education.quiztest.entity;

import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeQuizGroupQuestionParticipationExecutionChoiceId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "quiz_group_question_participation_execution_choice")
@IdClass(CompositeQuizGroupQuestionParticipationExecutionChoiceId.class)
public class QuizGroupQuestionParticipationExecutionChoice {

    @Id
    @Column(name = "question_id")
    private UUID questionId;

    @Id
    @Column(name = "quiz_group_id")
    private UUID quizGroupId;

    @Id
    @Column(name = "participation_user_login_id")
    private String participationUserLoginId;

    @Id
    @Column(name = "choice_answer_id")
    private UUID choiceAnswerId;

    @Column(name = "submission_id")
    private UUID submissionId;

    @Column(name="created_stamp")
    private Date createdStamp;

}
