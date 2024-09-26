package com.hust.baseweb.applications.education.quiztest.entity;

import java.util.Date;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeInteractiveQuizQuestionId;

@Getter
@Setter
@Entity
@Table(name = "interactive_quiz_question")
@IdClass(CompositeInteractiveQuizQuestionId.class)
public class InteractiveQuizQuestion {
    @Id
    @Column(name = "interactive_quiz_id")
    private UUID interactiveQuizId;

    @Id
    @Column(name = "quiz_question_id")
    private UUID questionId;

    @Column(name = "created_stamp")
    private Date createdStamp;

    @Column(name = "last_updated")
    private Date lastUpdated;
}
