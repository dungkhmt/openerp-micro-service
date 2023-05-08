package com.hust.baseweb.applications.education.quiztest.entity;

import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeEduTestQuizGroupParticipationAssignmentId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "edu_test_quiz_group_participation_assignment")
@IdClass(CompositeEduTestQuizGroupParticipationAssignmentId.class)
public class EduTestQuizGroupParticipationAssignment {

    @Id
    @Column(name = "quiz_group_id")
    private UUID quizGroupId;

    @Id
    @Column(name = "participation_user_login_id")
    private String participationUserLoginId;

    @Column(name="status_id")
    private String statusId;
}
