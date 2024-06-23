package com.hust.baseweb.applications.education.quiztest.entity;
import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.hust.baseweb.applications.education.entity.QuizChoiceAnswer;
import com.hust.baseweb.applications.education.entity.QuizQuestion;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeInteractiveQuizAnswerId;

@Getter
@Setter
@Entity
@Table(name = "interactive_quiz_answer")
@IdClass(CompositeInteractiveQuizAnswerId.class)

public class InteractiveQuizAnswer {
    @Id
    @Column(name = "interactive_quiz_id")
    private UUID interactiveQuizId;

    @Id
    @Column(name = "quiz_question_id")
    private UUID questionId;

    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "choice_answer_id")
    private UUID choiceAnswerId;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;

     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_question_id", insertable = false, updatable = false)
    private QuizQuestion quizQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "choice_answer_id", insertable = false, updatable = false)
    private QuizChoiceAnswer quizChoiceAnswer;
}
