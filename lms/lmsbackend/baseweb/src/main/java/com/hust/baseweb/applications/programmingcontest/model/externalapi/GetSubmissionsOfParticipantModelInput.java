package com.hust.baseweb.applications.programmingcontest.model.externalapi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSubmissionsOfParticipantModelInput {
    private String participantId;
}
