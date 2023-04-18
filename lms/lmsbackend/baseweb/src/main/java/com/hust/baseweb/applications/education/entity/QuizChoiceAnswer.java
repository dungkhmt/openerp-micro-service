package com.hust.baseweb.applications.education.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "quiz_choice_answer")
public class QuizChoiceAnswer {

    @Id
    @Column(name = "choice_answer_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID choiceAnswerId;

    @Column(name="choice_answer_code")
    private String choiceAnswerCode;

    @Column(name = "choice_answer_content")
    private String choiceAnswerContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", referencedColumnName = "question_id")
    private QuizQuestion quizQuestion;

    @Column(name = "is_correct_answer")
    private char isCorrectAnswer;

    @Column(name = "last_updated_stamp")
    private Date lastUpdatedStamp;

    @Column(name = "created_stamp")
    private Date createdStamp;
}
