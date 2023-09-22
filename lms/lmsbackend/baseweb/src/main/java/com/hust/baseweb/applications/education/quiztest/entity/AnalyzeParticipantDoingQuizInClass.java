package com.hust.baseweb.applications.education.quiztest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;
@Getter
@Setter
@Entity
@Table(name = "analyze_participant_doing_quiz_in_class")
public class AnalyzeParticipantDoingQuizInClass {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="class_id")
    private UUID classId;

    @Column(name="participant_userlogin_id")
    private String participantUserloginId;

    @Column(name="number_quiz_test")
    private int numberQuizTest;
    @Column(name="number_participation_select")
    private int numberParticipationSelect;
    @Column(name="number_correct")
    private int numberCorrect;
    @Column(name="number_correct_fastest")
    private int numberCorrectFastest;

    @Column(name="created_stamp")
    private Date createdStamp;
}
