package com.hust.baseweb.applications.programmingcontest.entity;

import com.hust.baseweb.applications.programmingcontest.composite.CompositeContestProblemId;
import com.hust.baseweb.applications.programmingcontest.composite.CompositeContestUserParticipantGroupId;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "contest_user_participant_group")
@IdClass(CompositeContestUserParticipantGroupId.class)
public class ContestUserParticipantGroup {

    @Id
    @Column(name = "contest_id")
    private String contestId;

    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "participant_id")
    private String participantId;

}
