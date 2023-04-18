package com.hust.baseweb.applications.education.quiztest.entity;

import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeEduQuizTestQuizQuestionId;
import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeTestQuizParticipationId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "edu_quiz_test_quiz_question")
@IdClass(CompositeEduQuizTestQuizQuestionId.class)
public class EduQuizTestQuizQuestion {
    public static final String STATUS_CREATED = "CREATED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    @Id
    @Column(name="test_id")
    private String testId;

    @Id
    @Column(name="question_id")
    private UUID questionId;

    @Column(name="created_by_user_login_id")
    private String createdByUserLoginId;

    @Column(name="status_id")
    private String statusId;

    @Column(name="created_stamp")
    private Date createdStamp;
}
