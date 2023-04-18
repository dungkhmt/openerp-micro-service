package com.hust.baseweb.applications.education.quiztest.entity;

import com.hust.baseweb.applications.education.quiztest.entity.compositeid.CompositeTestQuizParticipationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "edu_test_quiz_participant")
@IdClass(CompositeTestQuizParticipationId.class)
public class EduTestQuizParticipant {

    public static final String STATUS_REGISTERED = "STATUS_REGISTERED";
    public static final String STATUS_APPROVED = "STATUS_APPROVED";
    public static final String STATUS_REJECTED = "STATUS_REJECTED";

    public static final String ROLE_PARTICIPANT = "PARTICIPANT";
    public static final String ROLE_MANAGER = "MANAGER";
    public static final String ROLE_OWNER = "OWNER";

    @Id
    @Column(name = "test_id")
    private String testId;

    @Id
    @Column(name = "participant_user_login_id")
    private String participantUserLoginId;

    @Column(name = "status_id")
    private String statusId;

    @Column(name="permutation")
    private String permutation;


}
