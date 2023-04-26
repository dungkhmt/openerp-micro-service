package com.hust.baseweb.applications.education.quiztest.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "quiz_test_execution_submission")
public class QuizTestExecutionSubmission {
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_SOLVED = "SOLVED";

    @Id
    @Column(name="submission_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID submissionId;

    @Column(name = "question_id")
    private UUID questionId;

    @Column(name = "quiz_group_id")
    private UUID quizGroupId;

    @Column(name = "participation_user_login_id")
    private String participationUserLoginId;

    @Column(name = "choice_answer_ids")
    private String choiceAnswerIds;

    @Column(name = "status_id")
    private String statusId;

    @Column(name="created_stamp")
    private Date createdStamp;

    @Column(name="last_updated_stamp")
    private Date lastUpdatedStamp;

}
