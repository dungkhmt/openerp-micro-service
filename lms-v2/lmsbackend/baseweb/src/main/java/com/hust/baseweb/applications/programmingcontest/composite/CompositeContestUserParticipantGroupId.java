package com.hust.baseweb.applications.programmingcontest.composite;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CompositeContestUserParticipantGroupId implements Serializable {
    private String contestId;
    private String userId;
    private String participantId;

}
